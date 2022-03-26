package tech.kotlinx.knox

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tech.kotlinx.knox.adapter.MessageAdapter
import tech.kotlinx.knox.data.model.Datasource

class ChatFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        val messages = Datasource().loadMessages()
        val recyclerView=view.findViewById<RecyclerView>(R.id.message_view)
        recyclerView.adapter= context?.let { MessageAdapter(it, messages) }
        return view
    }
}