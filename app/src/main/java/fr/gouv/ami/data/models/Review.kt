package fr.gouv.ami.data.models

import com.google.gson.annotations.SerializedName

data class Review (
    @SerializedName("url")
    val url: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("number")
    val number: String,
    @SerializedName("description")
    val description: String?
)