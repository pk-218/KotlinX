package tech.kotlinx.knox.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import tech.kotlinx.knox.data.repository.RepositoryImpl
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class ChatViewModel() : ViewModel() {
    val TAG = "Chat View Model"

//    suspend fun getUserName(): String? {
//        return repository.getUserName().first()
//    }

    suspend fun sendMessage(msg : String?, receiverIpAddress : String?, receiverPort : Int) {
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

    suspend fun receiveMessage(vararg sockets: Socket): String? {
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