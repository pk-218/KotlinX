package tech.kotlinx.knox.data.repository

import tech.kotlinx.knox.data.model.Message
import tech.kotlinx.knox.data.model.MessageDao

class MessageRepository(private val messageDao : MessageDao) {
    fun getMesssages(ip : String) : ArrayList<Message> {
        return messageDao.readMessages(ip)
    }

    suspend fun addMessage(message : Message){
        messageDao.addMessage(message)
    }
}