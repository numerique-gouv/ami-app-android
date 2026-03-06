package fr.gouv.ami.data.models

import com.google.gson.annotations.SerializedName

data class AuthenticationOutput(
    @SerializedName("authenticated")
    val isAuthenticated: Boolean
)
