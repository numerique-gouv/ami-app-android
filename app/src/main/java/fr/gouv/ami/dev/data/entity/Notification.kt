package fr.gouv.ami.dev.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Notification (
    @PrimaryKey(autoGenerate = true) var uid: Int,
    var userId: Int,
    var message: String,
    var sender: String?,
    var title: String?,
    var id: Int?,
    var date: String?,
    var isNotified: Boolean
)