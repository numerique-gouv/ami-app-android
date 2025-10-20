package fr.gouv.ami.dev.data.repository

import fr.gouv.ami.dev.api.apiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

fun getNotificationKey() : Flow<Response<String>> {
    return flow {
        val response = apiService.getNotificationKey()
        emit(response)
    }
}