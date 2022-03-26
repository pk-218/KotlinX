package tech.kotlinx.knox.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.kotlinx.knox.data.model.Message
import kotlinx.coroutines.launch
import tech.kotlinx.knox.data.repository.RepositoryImpl
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import kotlin.collections.ArrayList
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val repository: RepositoryImpl): ViewModel() {
    val TAG = "Chat View Model"
    private var _messages = MutableLiveData<MutableList<Message>>(
        arrayListOf()
    )

    val messages : MutableLiveData<MutableList<Message>>
    get()= _messages

    var userName: MutableLiveData<String> = MutableLiveData()

    fun sendMessage(msg : String?, receiverIpAddress : String?, receiverPort : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val clientSocket : Socket = Socket(receiverIpAddress, receiverPort)
                val outToServer = clientSocket.getOutputStream()
                val output = PrintWriter(outToServer)
                output.println(msg)
                _messages.value?.add(Message(receiverIpAddress!!, msg, 0, Calendar.getInstance().time))
                Log.v(TAG, "Sent Message")
                output.flush()
                clientSocket.close()

            } catch(e : Exception) {
                e.printStackTrace()
            }
        }
    }

    //TODO:GET SENDER's IP
    private suspend fun receiveMessage(vararg sockets: Socket): String? {
        val job = viewModelScope.async(Dispatchers.IO) {
            var text: String? = null
            try {
                val input = BufferedReader(InputStreamReader(sockets[0].getInputStream()))
                text = input.readLine()
                _messages.value?.add(Message("", text, 1, Calendar.getInstance().time))
                Log.i(TAG, "Received => $text")
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            text

    fun getUserName() {
        viewModelScope.launch {
            repository.getUserName().collect {
                userName.postValue(it)
            }
        }
    }

    fun sendMessage(msg : String?, receiverIpAddress : String?, receiverPort : Int) {
        try {
            val clientSocket : Socket = Socket(receiverIpAddress, receiverPort)
            val outToServer = clientSocket.getOutputStream()
            val output = PrintWriter(outToServer)
            output.println(msg)
            output.flush()
            clientSocket.close()

        } catch(e : Exception) {
            e.printStackTrace()
        }
    }

    fun receiveMessage(vararg sockets: Socket): String? {
        var text : String? = null
        try {
            val input = BufferedReader(InputStreamReader(sockets[0].getInputStream()))
            text = input.readLine()
            Log.i(TAG, "Received => $text")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return text
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