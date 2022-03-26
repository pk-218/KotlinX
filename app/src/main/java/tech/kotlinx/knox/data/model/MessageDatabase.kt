package tech.kotlinx.knox.data.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Message::class], version = 1, exportSchema = false)
abstract class MessageDatabase : RoomDatabase() {

    abstract fun messageDao() : MessageDao

    companion object{
        @Volatile
        private var INSTANCE : MessageDatabase? = null

        fun getDatabase(context: Context) : MessageDatabase{
            val tempInstance = INSTANCE
            if(tempInstance!=null){
                return tempInstance
            }

            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MessageDatabase::class.java,
                    "message_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}