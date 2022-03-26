package tech.kotlinx.knox

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import tech.kotlinx.knox.adapter.MessageAdapter
import tech.kotlinx.knox.data.model.Datasource
import tech.kotlinx.knox.data.model.Message
import tech.kotlinx.knox.ui.viewmodels.ChatViewModel
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class ChatFragment() : Fragment() {

    private val viewModel: ChatViewModel by viewModels()
    private var port = 5000
    private var ipAddress = "0.0.0.0"
    private var messages : ArrayList<Message> = arrayListOf()
    private lateinit var recyclerView : RecyclerView
    private lateinit var textView : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        messages = Datasource().loadMessages()
        recyclerView=view.findViewById(R.id.message_view)
        //TODO: re-initialize port and ipAddress from safe args
        //rendering of messages
        recyclerView.adapter= context?.let { MessageAdapter(it, messages) }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var oppUserName : String? = ""
        try {
            val serverSocket : ServerSocket = ServerSocket(port)
            serverSocket.reuseAddress = true

            //TODO: send username

            while(!Thread.interrupted()) {
                val connectSocket : Socket = serverSocket.accept()
                //get username

                viewLifecycleOwner.lifecycleScope.launch {
                    if(oppUserName=="") {
                        oppUserName = viewModel.receiveMessage(connectSocket)
                        //text view set text
                        textView.text = oppUserName
                    }
                    else {
                        val text = viewModel.receiveMessage(connectSocket)
                        val msg : Message = Message(text, 1, Calendar.getInstance().time)
                        messages.add(msg)
                        recyclerView.adapter= context?.let { MessageAdapter(it, messages) }
                    }
                    //first text will be the username
                }
            }
            serverSocket.close()
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }

}