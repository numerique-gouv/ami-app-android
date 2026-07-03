package fr.gouv.ami.utils.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
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

    suspend fun <T> readData(
        preferenceKey: Preferences.Key<T>
    ): T? {
        return context.privateStorage.data
            .map { preferences ->
                preferences[preferenceKey]
            }
            .first()
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