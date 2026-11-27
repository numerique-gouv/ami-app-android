package fr.gouv.ami.data.repository

import fr.gouv.ami.api.apiService
import fr.gouv.ami.data.models.ServiceUrls
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

fun getServiceUrlsWithoutFollowupFlow() : Flow<Response<ServiceUrls>> {
    return flow {
        val response = apiService.getServiceUrlsWithoutFollowup()
        emit(response)
    }
}