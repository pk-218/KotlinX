package tech.kotlinx.knox

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
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
    private var myPort = 5000
    private var receiverPort = 5000
    private var receiverIpAddress = "0.0.0.0"
    private var messages : ArrayList<Message> = arrayListOf()
    private lateinit var recyclerView : RecyclerView
    private lateinit var textView : TextView
    private var myUserName : String? = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        val senderTextBox = view.findViewById<EditText>(R.id.edittext_chatbox)
        messages = Datasource().loadMessages()
        recyclerView=view.findViewById(R.id.message_view)

        //TODO: re-initialize receiverPort,receiverIpAddress and myUsername from safe args and local store

        //render messages
        recyclerView.adapter= context?.let { MessageAdapter(it, messages) }

        view.findViewById<ImageButton>(R.id.button_chatbox_send).setOnClickListener {
            if(!senderTextBox.text.isBlank())
            {
                val msg = Message(senderTextBox.text.toString(), 0, Calendar.getInstance().time)
                //TODO: msg sending logic
                messages.add(msg)
                recyclerView.adapter?.notifyItemInserted(messages.size - 1)
                recyclerView.scrollToPosition(messages.size-1)
                senderTextBox.text.clear()
            }
        }

        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var receiverUserName : String? = ""

        try {
            val serverSocket : ServerSocket = ServerSocket(myPort)
            serverSocket.reuseAddress = true

            //send username to receiver
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.sendMessage(myUserName, receiverIpAddress, receiverPort)
            }

            while(!Thread.interrupted()) {
                val connectSocket : Socket = serverSocket.accept()
                //get username
                viewLifecycleOwner.lifecycleScope.launch {
                    if(receiverUserName=="") {
                        receiverUserName = viewModel.receiveMessage(connectSocket)
                        //text view set text
                        textView.text = receiverUserName
                    }
                    else {
                        val text = viewModel.receiveMessage(connectSocket)
                        val msg : Message = Message(text, 1, Calendar.getInstance().time)
                        messages.add(msg)
                        recyclerView.adapter?.notifyItemInserted(messages.size - 1)
                        recyclerView.scrollToPosition(messages.size-1)
                    }
                }
            }
            serverSocket.close()
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }



}