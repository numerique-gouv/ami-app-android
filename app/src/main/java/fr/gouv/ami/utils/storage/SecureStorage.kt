package fr.gouv.ami.utils.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SecureStorage(val context: Context, val userStoreId: String) {
    val Context.encryptedStorage: DataStore<Preferences> by preferencesDataStore(name = "${userStoreId}_encrypted")
    val Context.authenticatedStorage: DataStore<Preferences> by preferencesDataStore(name = "${userStoreId}_authenticated")

    suspend fun <T> writeData(
        preferenceKey: Preferences.Key<T>,
        value: T,
        requireAuthentication: Boolean
    ) {
        if (requireAuthentication) {
            context.authenticatedStorage.edit { preferences ->
                preferences[preferenceKey] = value
            }
        } else {
            context.encryptedStorage.edit { preferences ->
                preferences[preferenceKey] = value
            }
        }
    }

    suspend fun <T> readData(
        preferenceKey: Preferences.Key<T>,
        requireAuthentication: Boolean
    ): T? {
        if (requireAuthentication) {
            return context.authenticatedStorage.data
                .map { preferences ->
                    preferences[preferenceKey]
                }
                .first()
        } else {
            return context.encryptedStorage.data
                .map { preferences ->
                    preferences[preferenceKey]
                }
                .first()
        }
    }

    suspend fun <T> deleteData(
        preferenceKey: Preferences.Key<T>,
        requireAuthentication: Boolean
    ) {
        if (requireAuthentication) {
            context.authenticatedStorage.edit { preferences ->
                preferences.remove(preferenceKey)
            }
        } else {
            context.encryptedStorage.edit { preferences ->
                preferences.remove(preferenceKey)
            }
        }
    }

    suspend fun  deleteAll(
        requireAuthentication: Boolean
    ) {
        if (requireAuthentication) {
            context.authenticatedStorage.edit { preferences ->
                preferences.clear()
            }
        } else {
            context.encryptedStorage.edit { preferences ->
                preferences.clear()
            }
        }
    }
}