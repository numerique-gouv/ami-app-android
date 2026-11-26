package fr.gouv.ami.data.models

import com.google.gson.annotations.SerializedName
import java.net.URL

data class ServiceUrls(
    @SerializedName("service_urls_without_followup")
    var serviceUrlsWithoutFollowup: List<URL>
)
