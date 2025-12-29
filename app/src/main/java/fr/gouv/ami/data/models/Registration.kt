package fr.gouv.ami.data.models

import com.google.gson.annotations.SerializedName

data class Registration(
    @SerializedName("id")
    var id: String,
    @SerializedName("user_id")
    var userId: String,
    @SerializedName("subscription")
    var subscription: Subscription,
    @SerializedName("created_at")
    var createdAt: String
)
