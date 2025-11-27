package fr.gouv.ami.data.models

import com.google.gson.annotations.SerializedName
import fr.gouv.ami.data.entity.NotificationEntity

data class Notification(
    @SerializedName("id")
    var id: String,
    @SerializedName("user_id")
    var userId: String,
    @SerializedName("message")
    var message: String,
    @SerializedName("sender")
    var sender: String?,
    @SerializedName("title")
    var title: String?,
    @SerializedName("unread")
    var unread: Boolean,
    @SerializedName("date")
    var date: String
) {
    fun toEntity(): NotificationEntity {
        return NotificationEntity(
            id = id,
            userId = userId,
            message = message,
            sender = sender,
            title = title,
            unread = unread,
            date = date,
            isNotified = false
        )
    }
}
