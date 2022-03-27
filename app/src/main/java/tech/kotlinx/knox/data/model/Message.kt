package tech.kotlinx.knox.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

//@Entity(tableName = "message_table")
data class Message(
//    @PrimaryKey(autoGenerate = true)
//    val id : Int,
    var ip : String,
    var message : String?,
    var type : Int?,
    var sentAt : Date?
) {
    fun isSent() : Boolean {
        return type == 0
    }

    fun getTime() : Date? {
        return sentAt
    }
}
