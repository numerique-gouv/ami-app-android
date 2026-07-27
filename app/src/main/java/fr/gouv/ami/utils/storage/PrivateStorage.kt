package fr.gouv.ami.utils.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import fr.gouv.ami.utils.Result
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PrivateStorage(val context: Context, val userStoreId: String) {

    //TODO replace "userStoreId" to variable
    companion object {
        val Context.privateStorage: DataStore<Preferences> by preferencesDataStore(name = "userStoreId")
    }

    suspend fun <T> writeData(preferenceKey: Preferences.Key<T>, value: T) {
        context.privateStorage.edit { preferences ->
            preferences[preferenceKey] = value
        }
    }

    @Throws
    suspend fun <T> readData(
        preferenceKey: Preferences.Key<T>
    ): Result<T, LocalStorageError> {
        return try {
            val value = context.privateStorage.data
                .map { preferences ->
                    preferences[preferenceKey]
                }
                .first()
            if (value != null) {
                Result.Success(value)
            } else {
                Result.Failure(LocalStorageError.KeyNotFound)
            }
        } catch (e: Exception) {
            Result.Failure(LocalStorageError(e))
        }
    }

    suspend fun <T> deleteData(
        preferenceKey: Preferences.Key<T>
    ) {
        context.privateStorage.edit { preferences ->
            preferences.remove(preferenceKey)
        }
    }

    suspend fun deleteAll() {
        context.privateStorage.edit { preferences ->
            preferences.clear()
        }
    }
}