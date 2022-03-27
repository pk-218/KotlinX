package tech.kotlinx.knox.ui.viewmodels

import android.os.Environment
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import tech.kotlinx.knox.data.model.Message
import tech.kotlinx.knox.data.repository.RepositoryImpl
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val repository: RepositoryImpl) : ViewModel() {
    private val TAG = "Chat View Model"
    private var _messages = MutableLiveData<MutableList<Message>>(
        arrayListOf()
    )

    val messages: MutableLiveData<MutableList<Message>>
        get() = _messages

    var userName: MutableLiveData<String> = MutableLiveData()
    var status : MutableLiveData<String> = MutableLiveData()

    fun sendMessage(msg: String?, receiverIpAddress: String?, receiverPort: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val clientSocket = Socket(receiverIpAddress, receiverPort)
                val outToServer = clientSocket.getOutputStream()
                val output = PrintWriter(outToServer)
                output.println(msg)
                _messages.value?.add(
                    Message(
                        receiverIpAddress!!,
                        msg,
                        0,
                        Calendar.getInstance().time
                    )
                )
                Log.v(TAG, "Sent Message")
                output.flush()
                clientSocket.close()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //TODO:GET SENDER's IP
    private fun receiveMessage(vararg sockets: Socket) {
        viewModelScope.launch(Dispatchers.IO) {
            var text: String?
            try {
                val input = BufferedReader(InputStreamReader(sockets[0].getInputStream()))
                text = input.readLine()
                if(text == "Offline" || text == "Online") {
                    status.postValue(text)
                }
                else if(text[0]=='@') {
                    userName.postValue(text.subSequence(1, text.length).toString())
                } else {
                    _messages.value?.add(Message("", text, 1, Calendar.getInstance().time))
                    Log.i(TAG, "Received => $text")
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    fun startServer(port: Int) {
        //TODO: send username
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val serverSocket = ServerSocket(port)
                serverSocket.reuseAddress = true
                Log.d(TAG, Thread.currentThread().name.toString())
                while (!Thread.interrupted()) {
                    val connectSocket: Socket = serverSocket.accept()
                    receiveMessage(connectSocket)
                }
                serverSocket.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun sendFile(filePath: String, receiverIpAddress : String, receiverPort : Int) {
        var path = filePath
        var filenameX : String?
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val clientSocket = Socket(receiverIpAddress, receiverPort+1)
                if (path[0] != '/') {
                    path = "/storage/emulated/0/$path"
                }
                Log.d(TAG, "doInBackground: Storage Here $path")
                val file = File(path)
                Log.d(TAG, file.name)
                if (path.isEmpty()) {
                    Log.d(TAG, "File is Empty!")
//                    this.coroutineContext.cancel() //Not sure about cancellation coroutines
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
                messages.value!!.add(Message("",
                    "New File Sent: $filenameX:$path", 0, Calendar.getInstance().time
                )) //adding message on sender's side
                outputStream.close()
                dataOutputStream.close()

                clientSocket.close()
            } catch(e : Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun receiveFile(vararg sockets: Socket) : String? {
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
                    Log.d(TAG, "File name and size = $fileName and $fileSize")
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

            _messages.value!!.add(Message("", "New File Received: $text", 1, Calendar.getInstance().time))
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
                    receiveFile(connectFileSocket)
                }
                fileSocket.close()
            } catch(e : Exception) {
                e.printStackTrace()
            }
        }
    }
}