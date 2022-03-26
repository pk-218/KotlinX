package tech.kotlinx.knox.data.model

import java.util.*

class Datasource {
    fun loadMessages():List<Message> {
        return listOf<Message>(
            Message("Hi, Ho",0, Date(2,2,2)),
            Message("Hi, Ho",1, Date(2,2,2)),
            Message("Hi",0, Date(2,2,2)),
            Message("Hi, Ho",1, Date(2,2,2)),
            Message("Hi",0, Date(2,2,2)),
            Message("Hi, Ho",0, Date(2,2,2)),
            Message("Hi, Ho",0, Date(2,2,2)),
            Message("Hi, Ho",0, Date(2,2,2)),
            Message("Hi, Ho",1, Date(2,2,2)),
            Message("Hi",0, Date(2,2,2)),
            Message("Hi, Ho",1, Date(2,2,2)),
            Message("Hi",0, Date(2,2,2)),
            Message("Hi, Ho",0, Date(2,2,2)),
            Message("Hi, Ho",0, Date(2,2,2)),
            Message("Hi, Ho",0, Date(2,2,2)),
            Message("Hi, Ho",1, Date(2,2,2)),
            Message("Hi",0, Date(2,2,2)),
            Message("Hi, Ho",1, Date(2,2,2)),
            Message("Hi",0, Date(2,2,2)),
            Message("Hi, Ho",0, Date(2,2,2)),
            Message("Hi, Ho",0, Date(2,2,2)),
            Message("Hi, Ho",0, Date(2,2,2)),
            Message("Hi, Ho",1, Date(2,2,2)),
            Message("Hi",0, Date(2,2,2)),
            Message("Hi, Ho",1, Date(2,2,2)),
            Message("Hi",0, Date(2,2,2)),
            Message("Hi, Ho",0, Date(2,2,2)),
            Message("Hi, Ho",0, Date(2,2,2)),
            Message("Hi, Ho",0, Date(2,2,2)),
            Message("Hi, Ho",1, Date(2,2,2)),
            Message("Hi",0, Date(2,2,2)),
            Message("Hi, Ho",1, Date(2,2,2)),
            Message("Hi",0, Date(2,2,2)),
            Message("Hi, Ho",0, Date(2,2,2)),
            Message("Hi, Ho",0, Date(2,2,2))
        )
    }
}