package fr.gouv.ami.dev.api

import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    /** notifications **/

    @GET("/notification-key")
    suspend fun getNotificationKey(): Response<String>

}