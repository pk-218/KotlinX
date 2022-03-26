package tech.kotlinx.knox.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.kotlinx.knox.data.model.Message
import tech.kotlinx.knox.data.repository.RepositoryImpl
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.*

class ChatViewModel() : ViewModel() {
    val TAG = "Chat View Model"

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

    fun startServer(port : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val serverSocket: ServerSocket = ServerSocket(port)
                serverSocket.reuseAddress = true
                //loop start
                Log.d(TAG, Thread.currentThread().name.toString())
                while(!Thread.interrupted()) {
                    val connectSocket: Socket = serverSocket.accept()
                    val text = receiveMessage(connectSocket)
                    //TODO: UI update
//                withContext(Dispatchers.Main) {
//
//                    message.value = text
//                }
                    val msg: Message = Message(viewModel.message.value, 1, Calendar.getInstance().time)
                    messages.add(msg)
                    //loop end
                    Log.d(TAG, text!!)
                }
                serverSocket.close()
            } catch(e : Exception) {
                e.printStackTrace()
            }
        }
    }

    var message : MutableLiveData<String> = MutableLiveData()
}