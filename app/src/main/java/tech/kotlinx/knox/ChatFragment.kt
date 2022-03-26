package tech.kotlinx.knox

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tech.kotlinx.knox.adapter.MessageAdapter
import tech.kotlinx.knox.data.model.Datasource
import tech.kotlinx.knox.data.model.Message
import tech.kotlinx.knox.databinding.FragmentChatBinding
import tech.kotlinx.knox.ui.viewmodels.ChatViewModel
import java.net.ServerSocket
import java.net.Socket
import java.util.Calendar

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private val viewModel: ChatViewModel by viewModels()
    private var myPort = 5000
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //get ip and address from args
        Log.d("ChatFragmentArgs", args.receiverIP + ":" + args.receiverPort.toString())
        viewModel.getUserName()
        myUserName = viewModel.userName.value
        Log.d("myUserName", myUserName.toString())
        // render messages
        messages = Datasource().loadMessages()
        binding.messageView.adapter = context?.let {
            MessageAdapter(it, viewModel.messages.value!!)
        }

        viewModel.messages.observe(viewLifecycleOwner) { newMessages ->
            with(binding) {
                messageView.adapter?.notifyItemInserted(newMessages.size - 1)
                messageView.scrollToPosition(newMessages.size - 1)
                binding.edittextChatbox.text.clear()
            }
        }

        //start server
        viewModel.startServer(myPort)
        binding.buttonChatboxSend.setOnClickListener {
            if (binding.edittextChatbox.text.isNotBlank()) {
                viewModel.sendMessage(binding.edittextChatbox.text.toString(), args.receiverIP, args.receiverPort)
            }
        }
//        var receiverUserName: String? = ""
//        //get live updates from live data and render on the UI
//        val msg: Message = Message(viewModel.message.value, 1, Calendar.getInstance().time)
//        messages.add(msg)
//        with(binding) {
//            messageView.adapter?.notifyItemInserted(messages.size - 1)
//            messageView.scrollToPosition(messages.size - 1)
//        }
    }

}