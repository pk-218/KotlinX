package tech.kotlinx.knox.data.model

import java.util.*

class Datasource {
    fun loadMessages():List<Message> {
        return listOf<Message>(
            Message("Hi",1, Date(2,2,2)),
            Message("Hi",1, Date(2,2,2)),
            Message("Hi",1, Date(2,2,2))
        )
    }
}