package fr.gouv.ami.dev.data.models

import com.google.gson.annotations.SerializedName

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
)

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
