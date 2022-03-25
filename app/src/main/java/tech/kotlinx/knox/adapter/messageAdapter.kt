package tech.kotlinx.knox.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tech.kotlinx.knox.R
import tech.kotlinx.knox.data.model.Message

class MessageAdapter(
    private val context: Context,
    private val messageList:List<Message>
    ) : RecyclerView.Adapter<MessageAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.message_box)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_box, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = messageList[position]
        holder.textView.text =  item.getMessage()
    }

    override fun getItemCount() = messageList.size
}