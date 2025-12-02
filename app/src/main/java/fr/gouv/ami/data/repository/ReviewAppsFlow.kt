package fr.gouv.ami.data.repository

import fr.gouv.ami.api.apiService
import fr.gouv.ami.data.models.Review
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

fun getReviewApps() : Flow<Response<MutableList<Review>>> {
    return flow {
        val response = apiService.getReviewApps()
        emit(response)
    }
}