package fr.gouv.ami.utils.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SecureStorage(val context: Context, val userStoreId: String) {
    
    //TODO replace "userStoreId" to variable
    companion object {
        val Context.encryptedStorage: DataStore<Preferences> by preferencesDataStore(name = "userStoreId_encrypted")
        val Context.authenticatedStorage: DataStore<Preferences> by preferencesDataStore(name = "userStoreId_authenticated")
    }

    private fun store(authenticationRequired: Boolean): DataStore<Preferences> {
        return if (authenticationRequired) context.authenticatedStorage else context.encryptedStorage
    }

    suspend fun <T> writeData(
        preferenceKey: Preferences.Key<T>,
        value: T,
        authenticationRequired: Boolean
    ) {
        store(authenticationRequired).edit { preferences ->
            preferences[preferenceKey] = value
        }
    }

    suspend fun <T> readData(
        preferenceKey: Preferences.Key<T>,
        authenticationRequired: Boolean
    ): T? {

        return store(authenticationRequired).data
            .map { preferences ->
                preferences[preferenceKey]
            }
            .first()
    }

    suspend fun <T> deleteData(
        preferenceKey: Preferences.Key<T>,
        authenticationRequired: Boolean
    ) {
        store(authenticationRequired).edit { preferences ->
            preferences.remove(preferenceKey)
        }
    }

    suspend fun deleteAll(
        authenticationRequired: Boolean
    ) {
        store(authenticationRequired).edit { preferences ->
            preferences.clear()
        }
    }
}