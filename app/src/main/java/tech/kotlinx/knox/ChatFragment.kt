package tech.kotlinx.knox

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import tech.kotlinx.knox.adapter.MessageAdapter
import tech.kotlinx.knox.data.model.Datasource
import tech.kotlinx.knox.data.model.Message
import tech.kotlinx.knox.databinding.FragmentChatBinding
import tech.kotlinx.knox.ui.viewmodels.ChatViewModel
import java.net.ServerSocket
import java.net.Socket
import java.util.Calendar

class ChatFragment : Fragment() {

    private val viewModel: ChatViewModel by viewModels()
    private var myPort = 5000
    private var messages: ArrayList<Message> = arrayListOf()
    private var myUserName: String? = ""
    private val args by navArgs<ChatFragmentArgs>()

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO: re-initialize receiverPort,receiverIpAddress and myUsername from safe args and local store
        Log.d("ChatFragmentArgs", args.receiverIP + ":" + args.receiverPort.toString())
//        viewModel = (activity as MainActivity).chatViewModel
//        viewModel.viewModelScope.launch {
//            myUserName = viewModel.getUserName()
//            Log.d("myUserName", myUserName.toString())
//        }
        // render messages
        messages = Datasource().loadMessages()
        binding.messageView.adapter = context?.let {
            MessageAdapter(it, messages)
        }

        binding.buttonChatboxSend.setOnClickListener {
            if (binding.edittextChatbox.text.isNotBlank()) {
                val msg =
                    Message(binding.edittextChatbox.text.toString(), 0, Calendar.getInstance().time)
                //TODO: msg sending logic
                messages.add(msg)
                with(binding) {
                    messageView.adapter?.notifyItemInserted(messages.size - 1)
                    messageView.scrollToPosition(messages.size - 1)
                    binding.edittextChatbox.text.clear()
                }
            }
        }
        var receiverUserName: String? = ""

        try {
            val serverSocket: ServerSocket = ServerSocket(myPort)
            serverSocket.reuseAddress = true

            //send username to receiver
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.sendMessage(myUserName, args.receiverIP, args.receiverPort)
            }

            while (!Thread.interrupted()) {
                val connectSocket: Socket = serverSocket.accept()
                //get username
                viewLifecycleOwner.lifecycleScope.launch {
                    if (receiverUserName == "") {
                        receiverUserName = viewModel.receiveMessage(connectSocket)
                        //text view set text
                        binding.textView.text = receiverUserName
                    } else {
                        val text = viewModel.receiveMessage(connectSocket)
                        val msg: Message = Message(text, 1, Calendar.getInstance().time)
                        messages.add(msg)
                        with(binding) {
                            messageView.adapter?.notifyItemInserted(messages.size - 1)
                            messageView.scrollToPosition(messages.size - 1)
                        }
                    }
                }
            }
            serverSocket.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}