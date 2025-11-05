package fr.gouv.ami.dev.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.gouv.ami.dev.data.entity.NotificationEntity

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notifications WHERE userId = :id")
    fun getAll(id: Int): List<NotificationEntity>

    @Query("SELECT * FROM notifications WHERE id = :id LIMIT 1")
    fun getById(id: Int): NotificationEntity

    @Query("SELECT * FROM notifications WHERE isNotified = 0")
    fun getNotNotified(): List<NotificationEntity>

    @Insert
    fun insert(vararg notificationEntity: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(notificationEntities: List<NotificationEntity>)

    @Query("DELETE FROM notifications")
    suspend fun deleteAll()

    @Query("DELETE FROM notifications WHERE userId = :id")
    suspend fun deleteByUser(id: Int)

    @Query("UPDATE notifications SET isNotified = :isNotified WHERE id = :id")
    fun setIsNotified(id: Int, isNotified: Boolean)
}