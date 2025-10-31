package fr.gouv.ami.dev.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import fr.gouv.ami.dev.data.dao.NotificationDao
import fr.gouv.ami.dev.data.entity.NotificationEntity

@Database(
    entities = [
        NotificationEntity::class
    ],
    version = 1
)

abstract class AppDataBase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}
