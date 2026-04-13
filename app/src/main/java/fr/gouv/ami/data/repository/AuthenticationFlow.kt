package fr.gouv.ami.data.repository

import fr.gouv.ami.api.apiService
import fr.gouv.ami.data.models.AuthenticationOutput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

fun checkAuth(token: String): Flow<Response<AuthenticationOutput>> {
    return flow {
        val response = apiService.checkAuth(token)
        emit(response)
    }
}
