package tech.kotlinx.knox.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tech.kotlinx.knox.R
import tech.kotlinx.knox.data.model.Message


class MessageAdapter(
    private val context: Context,
    private val messageList:List<Message>
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_MESSAGE_SENT = 1
    private val VIEW_TYPE_MESSAGE_RECEIVED = 2
    private val LATEST_TYPE_MESSAGE_SENT = 3
    private val LATEST_TYPE_MESSAGE_RECEIVED = 4

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
                return SentMessageHolder(adapterLayout.inflate(R.layout.sent_message_box, parent, false))
            }
            VIEW_TYPE_MESSAGE_RECEIVED -> {
                return ReceivedMessageHolder(adapterLayout.inflate(R.layout.received_message_box, parent, false))
            }
            LATEST_TYPE_MESSAGE_SENT -> {
                val view = adapterLayout.inflate(R.layout.sent_message_box, parent, false)
                view.startAnimation(animation)
                return SentMessageHolder(view)
            }
        }
        val view = adapterLayout.inflate(R.layout.received_message_box, parent, false)
        view.startAnimation(animation)
        return ReceivedMessageHolder(view)
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

    private class SentMessageHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.send_message_box)
        fun bind(message : Message)
        {
            val newMessage = message.message
            textView.text = newMessage
        }
    }

    private class ReceivedMessageHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.received_message_box)
        fun bind(message : Message)
        {
            val newMessage = message.message
            textView.text = newMessage
        }
    }
}