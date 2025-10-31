package fr.gouv.ami.dev.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity (
    var userId: Int,
    var message: String?,
    var sender: String?,
    var title: String?,
    @PrimaryKey var id: Int,
    var date: String?,
    var isNotified: Boolean = false
)