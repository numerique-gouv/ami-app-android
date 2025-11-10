package fr.gouv.ami.data.repository

import fr.gouv.ami.api.apiService
import fr.gouv.ami.data.models.Notification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

fun getNotificationKey() : Flow<Response<String>> {
    return flow {
        val response = apiService.getNotificationKey()
        emit(response)
    }
}

fun getNotifications(userId: String) : Flow<Response<List<Notification>>> {
    return flow {
        val response = apiService.getNotifications(userId)
        emit(response)
    }
}