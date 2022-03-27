package tech.kotlinx.knox.adapter
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import tech.kotlinx.knox.R
import tech.kotlinx.knox.data.model.Message
import java.io.File
import java.text.DateFormat.getTimeInstance


class MessageAdapter(
    private val context: Context,
    private val messageList:List<Message>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_MESSAGE_SENT = 1
    private val VIEW_TYPE_MESSAGE_RECEIVED = 2
    private val LATEST_TYPE_MESSAGE_SENT = 3
    private val LATEST_TYPE_MESSAGE_RECEIVED = 4
    private val mediaPlayer: MediaPlayer = MediaPlayer()

    override fun getItemViewType(position: Int): Int {
        val message: Message = messageList.get(position)
        if (message.isSent() && position != messageList.size - 1) return VIEW_TYPE_MESSAGE_SENT
        else if (!message.isSent() && position != messageList.size - 1) return VIEW_TYPE_MESSAGE_RECEIVED
        else if (message.isSent() && position == messageList.size - 1) return LATEST_TYPE_MESSAGE_SENT
        else if (!message.isSent() && position == messageList.size - 1) return LATEST_TYPE_MESSAGE_RECEIVED
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val animation: Animation = AnimationUtils.loadAnimation(context, androidx.appcompat.R.anim.abc_grow_fade_in_from_bottom)
        val adapterLayout = LayoutInflater.from(parent.context)
        when(viewType)
        {
            VIEW_TYPE_MESSAGE_SENT -> {
                return SentMessageHolder(adapterLayout.inflate(R.layout.sent_message_box, parent, false), context, mediaPlayer)
            }
            VIEW_TYPE_MESSAGE_RECEIVED -> {
                return ReceivedMessageHolder(adapterLayout.inflate(R.layout.received_message_box, parent, false), context, mediaPlayer)
            }
            LATEST_TYPE_MESSAGE_SENT -> {
                val view = adapterLayout.inflate(R.layout.sent_message_box, parent, false)
                view.startAnimation(animation)
                return SentMessageHolder(view, context, mediaPlayer)
            }
        }
        val view = adapterLayout.inflate(R.layout.received_message_box, parent, false)
        view.startAnimation(animation)
        return ReceivedMessageHolder(view, context, mediaPlayer)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message: Message = messageList[position]
        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT -> (holder as SentMessageHolder).bind(message)
            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedMessageHolder).bind(message)
            LATEST_TYPE_MESSAGE_SENT -> (holder as SentMessageHolder).bind(message)
            LATEST_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedMessageHolder).bind(message)
        }
    }

    override fun getItemCount() = messageList.size

    private class SentMessageHolder(private val view: View, private val context:Context, private var mediaPlayer: MediaPlayer) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.send_message_box)
        val timeText: TextView = view.findViewById(R.id.text_message_time)
        val messageImage: ImageView = view.findViewById(R.id.sent_image)
        val playButton: ImageView = view.findViewById(R.id.sent_play_button)
        val pauseButton: ImageView = view.findViewById(R.id.sent_pause_button)

        init {
            view.setOnClickListener(this::onClick);
        }

        fun bind(message : Message)
        {
            val sdf = getTimeInstance()
            val currentDateTimeString: String = sdf.format(message.getTime()!!)
            val newMessage = message.message
            textView.text = newMessage
            timeText.text=currentDateTimeString

            if (message.message!!.contains("New File Sent: ") &&
                (message.message!!.contains("png") || message.message!!.contains("jpg") || message.message!!.contains("jpeg"))
            ) {
                textView.text = ""
                val fileName: List<String> = message.message?.split(":")!!
                val stringBuilder = StringBuilder(fileName[1])
                stringBuilder.deleteCharAt(0)
                val path = stringBuilder.toString()
                val directory: String = Environment.getExternalStorageDirectory().toString() + "/Download/" + path
                val imgFile = File(directory)
                if (imgFile.exists()) {
                    Glide.with(context)
                        .load(directory)
                        .apply(RequestOptions().override(500,500))
                        .into(messageImage)
                }
                else if (message.message!!.contains("New File Received: ") && (message.message!!.contains("mp3")))
                {
                    if (mediaPlayer.isPlaying) {
                        playButton.visibility = View.INVISIBLE;
                        pauseButton.visibility = View.VISIBLE;
                    } else {
                        playButton.visibility = View.VISIBLE;
                        pauseButton.visibility = View.INVISIBLE;
                    }
                }
            }
        }

        fun onClick(view: View)
        {
            if (textView.text.toString().contains(".mp3") && textView.text.toString().contains("New File Received: ")
            ) {
                val message: Array<String> = textView.text.toString().split(":") as Array<String>
                var filename = message[1]
                filename = filename.trim { it <= ' ' }
                val path = Environment.getExternalStorageDirectory().toString() + "/Download/"
                val uri: Uri = Uri.parse(path + filename)
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    playButton.visibility = View.VISIBLE
                    pauseButton.visibility = View.INVISIBLE
                } else {
                    mediaPlayer = MediaPlayer.create(context, uri)
                    //Log.d(TAG, "onClick: " + context.getObbDir() + "/downloadFolder/" + path);
                    mediaPlayer.start()
                    playButton.visibility = View.INVISIBLE
                    pauseButton.visibility = View.VISIBLE
                }
            }
        }
    }

    private class ReceivedMessageHolder(private val view: View, private val context:Context, private var mediaPlayer: MediaPlayer) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.received_message_box)
        val timeText: TextView = view.findViewById(R.id.text_message_time_rec)
        val messageImage: ImageView = view.findViewById(R.id.received_image)
        val playButton: ImageView = view.findViewById(R.id.play_button_rec)
        val pauseButton: ImageView = view.findViewById(R.id.pause_button_rec)

        init {
            view.setOnClickListener(this::onClick);
        }

        fun bind(message : Message)
        {
            val sdf = getTimeInstance()
            val currentDateTimeString: String = sdf.format(message.getTime()!!)
            val newMessage = message.message
            textView.text = newMessage
            timeText.text=currentDateTimeString

            if (message.message!!.contains("New File Received: ") &&
                (message.message!!.contains("png") || message.message!!.contains("jpg") || message.message!!.contains("jpeg"))
            ) {
                textView.visibility = View.INVISIBLE
                val fileName: List<String> = message.message?.split(":")!!
                val stringBuilder = StringBuilder(fileName[1])
                stringBuilder.deleteCharAt(0)
                val path = stringBuilder.toString()
                val directory: String = Environment.getExternalStorageDirectory().toString() + "/Download/" + path
                val imgFile = File(directory)
                Log.d("ADAPTER", "${imgFile.totalSpace}")
                if (imgFile.exists()) {
                    Glide.with(context)
                        .load(directory)
                        .apply(RequestOptions().override(500,500))
                        .into(messageImage)
                }
                else if (message.message!!.contains("New File Received: ") && (message.message!!.contains("mp3")))
                {
                    if (mediaPlayer.isPlaying) {
                        playButton.visibility = View.INVISIBLE;
                        pauseButton.visibility = View.VISIBLE;
                    } else {
                        playButton.visibility = View.VISIBLE;
                        pauseButton.visibility = View.INVISIBLE;
                    }
                }
            }
        }

        fun onClick(view: View)
        {
            if (textView.text.toString().contains(".mp3") && textView.text.toString().contains("New File Received: ")
            ) {
                val message: Array<String> = textView.text.toString().split(":") as Array<String>
                var filename = message[1]
                filename = filename.trim { it <= ' ' }
                val path = Environment.getExternalStorageDirectory().toString() + "/Download/"
                val uri: Uri = Uri.parse(path + filename)
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    playButton.visibility = View.VISIBLE
                    pauseButton.visibility = View.INVISIBLE
                } else {
                    mediaPlayer = MediaPlayer.create(context, uri)
                    //Log.d(TAG, "onClick: " + context.getObbDir() + "/downloadFolder/" + path);
                    mediaPlayer.start()
                    playButton.visibility = View.INVISIBLE
                    pauseButton.visibility = View.VISIBLE
                }
            }
        }

    }
}
