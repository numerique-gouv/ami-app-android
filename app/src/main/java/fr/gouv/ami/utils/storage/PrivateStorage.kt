package fr.gouv.ami.utils.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import fr.gouv.ami.utils.Result
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PrivateStorage(val dataStore: DataStore<Preferences>) {

    suspend fun <T> writeData(preferenceKey: Preferences.Key<T>, value: T) {
        dataStore.edit { preferences ->
            preferences[preferenceKey] = value
        }
    }

    @Throws
    suspend fun <T> readData(
        preferenceKey: Preferences.Key<T>
    ): Result<T, LocalStorageError> {
        return try {
            val value = dataStore.data
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
        dataStore.edit { preferences ->
            preferences.remove(preferenceKey)
        }
    }

    suspend fun deleteAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

object DataStoreFactory {

    private val stores = mutableMapOf<String, DataStore<Preferences>>()

    @Synchronized
    fun get(
        context: Context,
        userStoreId: String
    ): DataStore<Preferences> {

        return stores.getOrPut(userStoreId) {
            PreferenceDataStoreFactory.create(
                produceFile = {
                    context.applicationContext.preferencesDataStoreFile(userStoreId)
                }
            )
        }
    }
}