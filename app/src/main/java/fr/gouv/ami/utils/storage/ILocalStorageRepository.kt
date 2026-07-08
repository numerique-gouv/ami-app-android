package fr.gouv.ami.utils.storage

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ILocalStorageRepository(val context: Context) : LocalStorageRepositoryProtocol() {
    private lateinit var privateStorage: PrivateStorage
    private lateinit var secureStorage: SecureStorage

    init {
        val userStoreId = "tmp" //TODO à changer
        privateStorage = PrivateStorage(context, userStoreId)
        secureStorage = SecureStorage(context, userStoreId)

        CoroutineScope(Dispatchers.IO).launch {
            secureStorage.encryptedStorageLog()
        }
        CoroutineScope(Dispatchers.IO).launch {
            secureStorage.authenticatedStorageLog()
        }
    }

    private suspend fun <T> writeData(
        key: String,
        value: T,
        keyFactory: (String) -> Preferences.Key<T>,
        secureLevel: KeyStoreManager.SecurityLevelType
    ): Boolean {
        when (secureLevel) {
            KeyStoreManager.SecurityLevelType.Private -> {
                privateStorage.writeData(keyFactory(key), value)
            }

            KeyStoreManager.SecurityLevelType.Encrypted -> {
                secureStorage.writeData(stringPreferencesKey(key), value.toString(), false)
            }

            KeyStoreManager.SecurityLevelType.Authenticated -> {
                secureStorage.writeData(stringPreferencesKey(key), value.toString(), true)
            }
        }
        return true
    }

    override suspend fun writeBool(
        key: String,
        value: Boolean,
        secureLevel: KeyStoreManager.SecurityLevelType
    ): Boolean {
        return writeData(key, value, ::booleanPreferencesKey, secureLevel)
    }

    override suspend fun writeInt(
        key: String,
        value: Int,
        secureLevel: KeyStoreManager.SecurityLevelType
    ): Boolean {
        return writeData(key, value, ::intPreferencesKey, secureLevel)
    }

    override suspend fun writeString(
        key: String,
        value: String,
        secureLevel: KeyStoreManager.SecurityLevelType
    ): Boolean {
        return writeData(key, value, ::stringPreferencesKey, secureLevel)
    }

    private suspend fun <T> readData(
        key: String,
        keyFactory: (String) -> Preferences.Key<T>,
        secureLevel: KeyStoreManager.SecurityLevelType,
        converter: (String) -> T?
    ): T? {
         when (secureLevel) {
            KeyStoreManager.SecurityLevelType.Private -> {
                return privateStorage.readData(keyFactory(key))
            }

            KeyStoreManager.SecurityLevelType.Encrypted -> {
                return secureStorage.readData(stringPreferencesKey(key), false)?.let(converter)
            }

            KeyStoreManager.SecurityLevelType.Authenticated -> {
                return secureStorage.readData(stringPreferencesKey(key), true)?.let(converter)
            }
        }
    }

    override suspend fun readBool(
        key: String,
        secureLevel: KeyStoreManager.SecurityLevelType
    ): Boolean? {
        return readData(key, ::booleanPreferencesKey, secureLevel, String::toBoolean)
    }

    override suspend fun readInt(
        key: String,
        secureLevel: KeyStoreManager.SecurityLevelType
    ): Int? {
        return readData(key, ::intPreferencesKey, secureLevel, String::toInt)
    }

    override suspend fun readString(
        key: String,
        secureLevel: KeyStoreManager.SecurityLevelType
    ): String? {
        return readData(key, ::stringPreferencesKey, secureLevel, String::toString)
    }

    private suspend fun <T> delete(
        key: String,
        keyFactory: (String) -> Preferences.Key<T>,
        secureLevel: KeyStoreManager.SecurityLevelType
    ): Boolean {
        when (secureLevel) {
            KeyStoreManager.SecurityLevelType.Private -> {
                privateStorage.deleteData(keyFactory(key))
            }

            KeyStoreManager.SecurityLevelType.Encrypted -> {
                secureStorage.deleteData(keyFactory(key), false)
            }

            KeyStoreManager.SecurityLevelType.Authenticated -> {
                secureStorage.deleteData(keyFactory(key), true)
            }
        }
        return true
    }

    override suspend fun deleteBool(
        key: String,
        secureLevel: KeyStoreManager.SecurityLevelType
    ): Boolean {
        return delete(key, ::booleanPreferencesKey, secureLevel)
    }

    override suspend fun deleteInt(
        key: String,
        secureLevel: KeyStoreManager.SecurityLevelType
    ): Boolean {
        return delete(key, ::intPreferencesKey, secureLevel)
    }

    override suspend fun deleteString(
        key: String,
        secureLevel: KeyStoreManager.SecurityLevelType
    ): Boolean {
        return delete(key, ::stringPreferencesKey, secureLevel)
    }

    override suspend fun deleteAll(secureLevel: KeyStoreManager.SecurityLevelType): Boolean {
        when (secureLevel) {
            KeyStoreManager.SecurityLevelType.Private -> {
                privateStorage.deleteAll()
            }

            KeyStoreManager.SecurityLevelType.Encrypted -> {
                secureStorage.deleteAll(false)
            }

            KeyStoreManager.SecurityLevelType.Authenticated -> {
                secureStorage.deleteAll(true)
            }
        }
        return true
    }
}