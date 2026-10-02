package fr.gouv.ami.data.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

/**
Maps a web URL pattern to its corresponding alias.
The web app provides these mappings so the native app can handle
navigation to promoted pages instead of the web app navigating to them.

Example:
{ "pattern": "/#/welcome/notifications", "alias": "welcome:notifications" }


The data is received from the web app as a JSON array of these objects.
 */
@Serializable
data class UrlAliases(
    @SerializedName("pattern")
    val pattern: String,
    @SerializedName("alias")
    val alias: String
)
