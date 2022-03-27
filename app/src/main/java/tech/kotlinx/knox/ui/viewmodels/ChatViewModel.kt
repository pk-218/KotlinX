package tech.kotlinx.knox.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.kotlinx.knox.data.model.Message
import tech.kotlinx.knox.data.repository.RepositoryImpl
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val repository: RepositoryImpl) : ViewModel() {
    val TAG = "Chat View Model"
    private var _messages = MutableLiveData<MutableList<Message>>(
        arrayListOf()
    )

    var receiverUserName : MutableLiveData<String> = MutableLiveData("No data")
    var message: MutableLiveData<String> = MutableLiveData()
    val messages: MutableLiveData<MutableList<Message>>
        get() = _messages

    var userName: MutableLiveData<String> = MutableLiveData()

    fun sendMessage(msg: String?, receiverIpAddress: String?, receiverPort: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val clientSocket: Socket = Socket(receiverIpAddress, receiverPort)
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
            var text: String? = null
            try {
                val input = BufferedReader(InputStreamReader(sockets[0].getInputStream()))
                text = input.readLine()
                if(receiverUserName.value == "No data") {
                    //get receiver's Username
                        Log.d(TAG, "RECEIVER'S USERNAME : $receiverUserName")
                    receiverUserName.value = text
                } else {
                    _messages.value?.add(Message("", text, 1, Calendar.getInstance().time))
                }
                Log.i(TAG, "Received => $text")
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    fun startServer(port: Int, userName : String, receiverIpAddress: String?, receiverPort: Int) {
        //TODO: send username
        viewModelScope.launch(Dispatchers.IO) {
            try {
                sendMessage(userName, receiverIpAddress, receiverPort)
                val serverSocket: ServerSocket = ServerSocket(port)
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
}