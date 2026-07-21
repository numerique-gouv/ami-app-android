package fr.gouv.ami.utils.storage

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.crypto.Cipher

class SecureStorage(val context: Context, val userStoreId: String) {

    //TODO replace "userStoreId" to variable
    companion object {
        val Context.encryptedStorage: DataStore<Preferences> by preferencesDataStore(name = "userStoreId_encrypted")
        val Context.authenticatedStorage: DataStore<Preferences> by preferencesDataStore(name = "userStoreId_authenticated")
    }

    val cipherManager = CipherManagerImpl()

    fun store(authenticationRequired: Boolean): DataStore<Preferences> {
        return if (authenticationRequired) context.authenticatedStorage else context.encryptedStorage
    }

    @RequiresApi(Build.VERSION_CODES.P)
    suspend fun writeData(
        preferenceKey: Preferences.Key<String>,
        value: String,
        authenticationRequired: Boolean,
        cipher: Cipher?
    ) {
        val valueEncrypted = cipherManager.encrypt(value, authenticationRequired, cipher)
        store(authenticationRequired).edit { preferences ->
            preferences[preferenceKey] = valueEncrypted
        }
    }

    suspend fun readData(
        preferenceKey: Preferences.Key<String>,
        authenticationRequired: Boolean,
        cipher: Cipher?
    ): String? {

        val valueEncrypted = store(authenticationRequired).data
            .map { preferences ->
                preferences[preferenceKey]
            }
            .first()
        return if (valueEncrypted.isNullOrEmpty()) null else cipherManager.decrypt(
            valueEncrypted,
            authenticationRequired,
            cipher
        )
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