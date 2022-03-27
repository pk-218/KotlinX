package tech.kotlinx.knox

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.loader.content.CursorLoader
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import tech.kotlinx.knox.adapter.MessageAdapter
import tech.kotlinx.knox.databinding.FragmentChatBinding
import tech.kotlinx.knox.ui.viewmodels.ChatViewModel
import java.io.File


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


        //file attachment send

        binding.fileSend.setOnClickListener(){
            Log.d("filesend", "clicked on file send")
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data?.data //The uri with the location of the file
            Log.d("URI of Selected File", selectedFile.toString())
            if (selectedFile != null) {
                selectedFile.getPath()
                Log.d("Path of file ",selectedFile.getPath().toString())
            }




        }
    }



    // Function for displaying an AlertDialogue for choosing an image
//    private fun selectImage() {
//        val choice = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
//        val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
//        myAlertDialog.setTitle("Select Image")
//        myAlertDialog.setItems(choice, DialogInterface.OnClickListener { dialog, item ->
//            when {
//                // Select "Choose from Gallery" to pick image from gallery
//                choice[item] == "Choose from Gallery" -> {
//                    val pickFromGallery = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                    pickFromGallery.type = "/image"
//                    startActivityForResult(pickFromGallery, 1)
//                }
//                // Select "Take Photo" to take a photo
//                choice[item] == "Take Photo" -> {
//                    val cameraPicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                    startActivityForResult(cameraPicture, 0)
//                }
//                // Select "Cancel" to cancel the task
//                choice[item] == "Cancel" -> {
//                    myAlertDialog.dismiss()
//                }
//            }
//        })
//        myAlertDialog.show()
//    }




}