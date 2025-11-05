package fr.gouv.ami.dev.data.models

import com.google.gson.annotations.SerializedName
import fr.gouv.ami.dev.data.entity.NotificationEntity

data class Notification(
    @SerializedName("user_id")
    var userId: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("sender")
    var sender: String?,
    @SerializedName("title")
    var title: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("date")
    var date: String?
) {
    fun toEntity(): NotificationEntity {
        return NotificationEntity(
            userId = userId,
            message = message,
            sender = sender,
            title = title,
            id = id ?: 0,
            date = date,
            isNotified = false
        )
    }
}

data class NotificationCreate(
    @SerializedName("user_id")
    var userId: Int,
    @SerializedName("message")
    var message: String,
    @SerializedName("sender")
    var sender: String?,
    @SerializedName("title")
    var title: String?
)
