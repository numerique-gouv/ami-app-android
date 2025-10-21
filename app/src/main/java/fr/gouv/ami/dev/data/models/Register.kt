package fr.gouv.ami.dev.data.models

import com.google.gson.annotations.SerializedName

data class Register(
    @SerializedName("user_id")
    var userId: Int,
    @SerializedName("subscription")
    var subscription: Subscription
)
