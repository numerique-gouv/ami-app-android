package fr.gouv.ami.dev.api

import fr.gouv.ami.dev.data.models.Notification
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    /** notifications **/

    @GET("/notification-key")
    suspend fun getNotificationKey(): Response<String>

    @GET("/api/v1/users/{user_id}/notifications")
    suspend fun getNotifications(
        @Path("user_id") userId: String
    ): Response<List<Notification>>

}