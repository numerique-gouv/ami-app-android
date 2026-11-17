package fr.gouv.ami.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UrlNavigation(
    @SerialName("goto_url")
    var gotoUrl: String
)