package fr.gouv.ami.api

import fr.gouv.ami.data.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("/api/v1/users/registrations")
    suspend fun registrations(
        @Header("Authorization") token: String,
        @Body subscription: SubscriptionRequest
    ): Response<Registration>

    /** Review App **/

    @GET("/dev-utils/review-apps")
    suspend fun getReviewApps(): Response<MutableList<Review>>

}
