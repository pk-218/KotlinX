package tech.kotlinx.knox.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import tech.kotlinx.knox.data.repository.RepositoryImpl
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val repository: RepositoryImpl): ViewModel() {
    val TAG = "Chat View Model"

    var userName: MutableLiveData<String> = MutableLiveData()

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


}