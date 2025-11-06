package fr.gouv.ami.dev.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity (
    @PrimaryKey(autoGenerate = true) val uId: Int,
    var userId: String,
    var message: String?,
    var sender: String?,
    var title: String?,
    var id: String,
    var date: String?,
    var isNotified: Boolean = false
)