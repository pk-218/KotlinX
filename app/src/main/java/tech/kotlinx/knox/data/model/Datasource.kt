package tech.kotlinx.knox.data.model

import java.util.*
import kotlin.collections.ArrayList

class Datasource {
    fun loadMessages():ArrayList<Message> {
        val list : ArrayList<Message> = arrayListOf()
        list.add(Message("","Hi", 1, Date(2, 2, 2)))
        list.add(Message("","Hi", 1, Date(2, 2, 2)))
        list.add(Message("","Hi", 1, Date(2, 2, 2)))
        return list
    }
}