package fr.gouv.ami.dev.data.models

import com.google.gson.annotations.SerializedName

data class Subscription(
    @SerializedName("keys")
    var keys: Keys,
    @SerializedName("endpoint")
    var endpoint: String
)

data class Keys(
    @SerializedName("auth")
    var auth: String,
    @SerializedName("p256dh")
    var p256dh: String
)
