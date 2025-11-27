package fr.gouv.ami.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity (
    @PrimaryKey var id: String,
    var userId: String,
    var message: String,
    var sender: String?,
    var title: String?,
    var unread: Boolean,
    var date: String?,
    var isNotified: Boolean = false
)