package fr.gouv.ami.dev.data.models

import com.google.gson.annotations.SerializedName

data class Registration(
    @SerializedName("user_id")
    var userId: String,
    @SerializedName("subscription")
    var subscription: Subscription,
    @SerializedName("id")
    var id: String?,
    @SerializedName("created_at")
    var createdAt: String?
)
