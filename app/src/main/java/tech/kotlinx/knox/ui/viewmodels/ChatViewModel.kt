package tech.kotlinx.knox.ui.viewmodels

import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.*
import java.net.ServerSocket
import java.net.Socket

class ChatViewModel() : ViewModel() {
    val TAG = "Chat View Model"
    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result

//    suspend fun getUserName(): String? {
//        return repository.getUserName().first()
//    }

    fun sendMessage(msg : String?, receiverIpAddress : String?, receiverPort : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val clientSocket : Socket = Socket(receiverIpAddress, receiverPort)
                val outToServer = clientSocket.getOutputStream()
                val output = PrintWriter(outToServer)
                output.println(msg)
                Log.v(TAG, "Sent Message")
                output.flush()
                clientSocket.close()

            } catch(e : Exception) {
                e.printStackTrace()
            }
        }
    }


    private suspend fun receiveMessage(vararg sockets: Socket): String? {
        val job = viewModelScope.async(Dispatchers.IO) {
            var text: String? = null
            try {
                val input = BufferedReader(InputStreamReader(sockets[0].getInputStream()))
                text = input.readLine()
                Log.i(TAG, "Received => $text")
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            text
        }
        return job.await()
    }

    fun startServer(port : Int) { //chat
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val serverSocket: ServerSocket = ServerSocket(port)
                serverSocket.reuseAddress = true
                Log.d(TAG, Thread.currentThread().name.toString())
                while(!Thread.interrupted()) {
                    val connectSocket: Socket = serverSocket.accept()
                    val text = receiveMessage(connectSocket)
                    //TODO: UI update
                    _result.value = text!! //live data updated value
//                withContext(Dispatchers.Main) {
//
//                    message.value = text
//                }
                    Log.d(TAG, text)
                }
                serverSocket.close()
            } catch(e : Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sendFile(filePath: String, receiverIpAddress : String, receiverPort : Int) {
        var path = filePath
        var filenameX = ""
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val clientSocket: Socket = Socket(receiverIpAddress, receiverPort)
                if (path[0] != '/') {
                    path = "/storage/emulated/0/$path"
                }
                Log.d(TAG, "doInBackground: Storage Here $path")
                val file = File(path)
                if (path.isEmpty()) {
                    this.coroutineContext.cancel() //Not sure about cancellation corountines
                }
                Log.d(TAG, "doInBackground: $path")

                val fileInputStream = FileInputStream(file)

                val fileSize = file.length()
                val byteArray = ByteArray(fileSize.toInt())

                val dataInputStream = DataInputStream(fileInputStream)
                dataInputStream.readFully(byteArray, 0, byteArray.size)

                val outputStream = clientSocket.getOutputStream()

                val dataOutputStream = DataOutputStream(outputStream)
                dataOutputStream.writeUTF(file.name)
                dataOutputStream.writeLong(byteArray.size.toLong())

                filenameX = file.name

                dataOutputStream.write(byteArray, 0, byteArray.size)
                dataOutputStream.flush()

                outputStream.write(byteArray, 0, byteArray.size)
                outputStream.flush()

                Log.d(TAG, "Sent File")

                outputStream.close()
                dataOutputStream.close()

                clientSocket.close()
            } catch(e : Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun receiveFile(vararg sockets: Socket) : String? {
        var text : String? = ""
        val job = viewModelScope.async(Dispatchers.IO) {
            try {
                val testDirectory = Environment.getExternalStorageDirectory()
                if (!testDirectory.exists()) testDirectory.mkdirs()
                try {
                    val inputStream = sockets[0].getInputStream()
                    val dataInputStream = DataInputStream(inputStream)
                    val fileName = dataInputStream.readUTF()
                    val outputFile = File("$testDirectory/Download/", fileName)
                    text = fileName
                    val outputStream: OutputStream = BufferedOutputStream(FileOutputStream(outputFile))
                    var fileSize = dataInputStream.readLong()
                    var bytesRead: Int
                    val byteArray = ByteArray(8192 * 16)
                    while (fileSize > 0) {
                        bytesRead = dataInputStream.read(byteArray, 0, kotlin.math.min(
                            byteArray.size.toLong(),
                            fileSize
                        ).toInt())
                        if(bytesRead==-1) {
                            break
                        }
                        outputStream.write(byteArray, 0, bytesRead)
                        fileSize -= bytesRead.toLong()
                    }
                    inputStream.close()
                    dataInputStream.close()
                    outputStream.flush()
                    outputStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            text
        }
        return job.await()
    }

    fun startFileServer(port : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fileSocket = ServerSocket(port + 1)
                Log.d(TAG, "run: " + fileSocket.localPort)
                fileSocket.reuseAddress = true
                Log.d(TAG, Thread.currentThread().name.toString())
                while(!Thread.interrupted()) {
                    val connectFileSocket : Socket = fileSocket.accept()
                    Log.d(TAG, "run: File Opened")

                }
                fileSocket.close()
            } catch(e : Exception) {
                e.printStackTrace()
            }
        }
    }
}