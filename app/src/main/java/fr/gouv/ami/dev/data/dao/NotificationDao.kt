package fr.gouv.ami.dev.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.gouv.ami.dev.data.entity.NotificationEntity

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notifications WHERE userId = :id")
    fun getAll(id: String): List<NotificationEntity>

    @Query("SELECT * FROM notifications WHERE id = :id LIMIT 1")
    fun getById(id: String): NotificationEntity

    @Query("SELECT * FROM notifications WHERE isNotified = 0")
    fun getNotNotified(): List<NotificationEntity>

    @Insert
    fun insert(vararg notificationEntity: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(notificationEntities: List<NotificationEntity>)

    @Query("DELETE FROM notifications")
    fun deleteAll()

    @Query("DELETE FROM notifications WHERE userId = :id")
    fun deleteByUser(id: String)

    @Query("UPDATE notifications SET isNotified = :isNotified WHERE id = :id")
    fun setIsNotified(id: String, isNotified: Boolean)
}