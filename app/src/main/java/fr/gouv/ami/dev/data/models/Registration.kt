package fr.gouv.ami.dev.data.models

import com.google.gson.annotations.SerializedName

data class Registrations(
    
)

data class Registration(
    @SerializedName("user_id")
    var userId: Int,
    @SerializedName("subscription")
    var subscription: Subscription,
    @SerializedName("id")
    var id: Int,
    @SerializedName("created_at")
    var createdAt: String
)
