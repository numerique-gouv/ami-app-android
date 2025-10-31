package fr.gouv.ami.dev.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.gouv.ami.dev.data.entity.Notification

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notification")
    fun getAll(): List<Notification>

    @Query("SELECT * FROM notification WHERE userId = :id")
    fun getByUserId(id: Int): List<Notification>

    @Query("SELECT * FROM notification WHERE id = :id LIMIT 1")
    fun getById(id: Int): Notification

    @Insert
    fun insert(vararg notification: Notification)

    @Insert
    fun insertAll(vararg notifications: List<Notification>)

    @Query("DELETE FROM notification")
    suspend fun deleteAll()

    @Query("DELETE FROM notification WHERE userId = :id")
    suspend fun deleteByUser(id: Int)

    @Query("UPDATE notification SET isNotified = :isNotified WHERE id = :id")
    fun setIsNotified(id: Int, isNotified: Boolean)
}