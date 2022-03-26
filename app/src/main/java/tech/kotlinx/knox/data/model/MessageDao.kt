package tech.kotlinx.knox.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MessageDao {

    @Insert
    suspend fun addMessage( message: Message )

    @Query("SELECT * FROM message_table WHERE ip = :_ip ORDER BY sentAt ASC")
    fun readMessages( _ip : String ) : ArrayList<Message>
}