package fr.gouv.ami.utils.storage

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import fr.gouv.ami.utils.Result
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.crypto.Cipher

class SecureStorage(
    val encryptedStorage: DataStore<Preferences>,
    val authenticatedStorage: DataStore<Preferences>
) {

    val cipherManager = CipherManagerImpl()

    fun store(authenticationRequired: Boolean): DataStore<Preferences> {
        return if (authenticationRequired) authenticatedStorage else encryptedStorage
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
    ): Result<String, LocalStorageError> {

        return try {
            val valueEncrypted = store(authenticationRequired).data
                .map { preferences ->
                    preferences[preferenceKey]
                }
                .first()
            if (valueEncrypted != null) {
                val value = cipherManager.decrypt(
                    valueEncrypted,
                    authenticationRequired,
                    cipher
                )
                return Result.Success(value)
            } else {
                return Result.Failure(LocalStorageError.KeyNotFound)
            }
        } catch (e: Exception) {
            Result.Failure(LocalStorageError(e))
        }
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