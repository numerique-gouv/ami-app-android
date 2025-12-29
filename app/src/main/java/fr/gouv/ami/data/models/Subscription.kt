package fr.gouv.ami.data.models

import com.google.gson.annotations.SerializedName

data class Subscription(
    @SerializedName("fcm_token")
    var fcmToken: String,
    @SerializedName("device_id")
    var deviceId: String,
    @SerializedName("platform")
    var platform: String,
    @SerializedName("app_version")
    var appVersion: String,
    @SerializedName("model")
    var model: String
)
