package tech.kotlinx.knox

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import tech.kotlinx.knox.adapter.MessageAdapter
import tech.kotlinx.knox.databinding.FragmentChatBinding
import tech.kotlinx.knox.ui.viewmodels.ChatViewModel

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

        // get receiver IP address and port from args
        Log.d("ChatFragmentArgs", args.receiverIP + ":" + args.receiverPort.toString())
        myUserName = viewModel.userName.value
        Log.d("myUserName", myUserName.toString())
        // render messages
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

        // start server
        viewModel.startServer(myPort)
        binding.buttonChatboxSend.setOnClickListener {
            if (binding.edittextChatbox.text.isNotBlank()) {
                viewModel.sendMessage(
                    binding.edittextChatbox.text.toString(),
                    args.receiverIP,
                    args.receiverPort
                )
                binding.edittextChatbox.text.clear()
            }
        }
    }

}