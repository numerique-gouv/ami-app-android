package fr.gouv.ami.api

import fr.gouv.ami.data.models.Notification
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    /** notifications **/

    @GET("/notification-key")
    suspend fun getNotificationKey(): Response<String>

    @GET("/api/v1/users/{user_id}/notifications")
    suspend fun getNotifications(@Path("userId") userId: String): Response<List<Notification>>

    /** Review App **/

    @GET("/dev-utils/review-apps")
    suspend fun getReviewApps(): Response<MutableList<List<String>>>

}