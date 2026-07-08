package fr.gouv.ami.utils.storage

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlin.collections.component1

class SecureStorage(val context: Context, val userStoreId: String) {

    //TODO replace "userStoreId" to variable
    companion object {
        val Context.encryptedStorage: DataStore<Preferences> by preferencesDataStore(name = "userStoreId_encrypted")
        val Context.authenticatedStorage: DataStore<Preferences> by preferencesDataStore(name = "userStoreId_authenticated")
    }

    val cipherManager = CipherManagerImpl()

    private fun store(authenticationRequired: Boolean): DataStore<Preferences> {
        return if (authenticationRequired) context.authenticatedStorage else context.encryptedStorage
    }

    suspend fun encryptedStorageLog() {
        context.encryptedStorage.data.first().asMap().forEach { (key, value) ->
            val decryptedValue = cipherManager.decrypt(value.toString(), false)
            Log.d("encryptedStorage log", "${key.name} = $decryptedValue")
        }
    }

    suspend fun authenticatedStorageLog() {
        context.authenticatedStorage.data.first().asMap().forEach { (key, value) ->
            Log.d("authenticatedStorage log", "${key.name} = $value")
        }
    }

    suspend fun writeData(
        preferenceKey: Preferences.Key<String>,
        value: String,
        authenticationRequired: Boolean
    ) {
        val valueEncrypted = cipherManager.encrypt(value, authenticationRequired)
        store(authenticationRequired).edit { preferences ->
            preferences[preferenceKey] = valueEncrypted
        }
    }

    suspend fun readData(
        preferenceKey: Preferences.Key<String>,
        authenticationRequired: Boolean
    ): String? {

        val valueEncrypted = store(authenticationRequired).data
            .map { preferences ->
                preferences[preferenceKey]
            }
            .first()
        return if (valueEncrypted.isNullOrEmpty()) null else cipherManager.decrypt(valueEncrypted, authenticationRequired)
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