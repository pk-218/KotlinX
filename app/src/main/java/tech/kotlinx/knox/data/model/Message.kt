package tech.kotlinx.knox.data.model
import androidx.room.Entity
import java.util.*

@Entity(tableName = "message_table")
data class Message(
    private var ip : String,
    private var message : String?,
    private var type : Int?,
    private var sentAt : Date?
) {
    fun getMessage() : String? {
        return message
    }

    fun isSent() : Boolean {
        return type == 0
    }

    fun getType() : Int? {
        return type
    }

    fun getTime() : Date? {
        return sentAt
    }
}
