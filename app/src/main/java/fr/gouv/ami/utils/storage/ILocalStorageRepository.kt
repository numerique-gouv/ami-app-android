package fr.gouv.ami.utils.storage

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

class ILocalStorageRepository(val context: Context) : LocalStorageRepositoryProtocol() {
    private lateinit var privateStorage: PrivateStorage
    private lateinit var secureStorage: SecureStorage

    init {
        val userStoreId = "tmp" //TODO à changer
        privateStorage = PrivateStorage(context, userStoreId)
        secureStorage = SecureStorage(context, userStoreId)
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
                secureStorage.writeData(keyFactory(key), value, false)
            }

            KeyStoreManager.SecurityLevelType.Authenticated -> {
                secureStorage.writeData(keyFactory(key), value, true)
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
        secureLevel: KeyStoreManager.SecurityLevelType
    ): Boolean {
        when (secureLevel) {
            KeyStoreManager.SecurityLevelType.Private -> {
                privateStorage.readData(keyFactory(key))
            }

            KeyStoreManager.SecurityLevelType.Encrypted -> {
                secureStorage.readData(keyFactory(key), false)
            }

            KeyStoreManager.SecurityLevelType.Authenticated -> {
                secureStorage.readData(keyFactory(key), true)
            }
        }
        return true
    }

    override suspend fun readBool(
        key: String,
        secureLevel: KeyStoreManager.SecurityLevelType
    ): Boolean {
        return readData(key, ::booleanPreferencesKey, secureLevel)
    }

    override suspend fun readInt(
        key: String,
        secureLevel: KeyStoreManager.SecurityLevelType
    ): Boolean {
        return readData(key, ::intPreferencesKey, secureLevel)
    }

    override suspend fun readString(
        key: String,
        secureLevel: KeyStoreManager.SecurityLevelType
    ): Boolean {
        return readData(key, ::stringPreferencesKey, secureLevel)
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