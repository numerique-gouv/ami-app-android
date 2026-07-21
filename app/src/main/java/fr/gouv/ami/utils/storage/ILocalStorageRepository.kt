package fr.gouv.ami.utils.storage

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.crypto.Cipher

class ILocalStorageRepository(val context: Context) : LocalStorageRepositoryProtocol() {
    private lateinit var privateStorage: PrivateStorage
    private lateinit var secureStorage: SecureStorage

    init {
        val userStoreId = "tmp" //TODO à changer
        privateStorage = PrivateStorage(context, userStoreId)
        secureStorage = SecureStorage(context, userStoreId)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private suspend fun <T> writeData(
        key: String,
        value: T,
        keyFactory: (String) -> Preferences.Key<T>,
        secureLevel: KeyStoreManager.SecurityLevelType,
        cipher: Cipher?,
    ): Boolean {
        when (secureLevel) {
            KeyStoreManager.SecurityLevelType.Private -> {
                privateStorage.writeData(keyFactory(key), value)
            }

            KeyStoreManager.SecurityLevelType.Encrypted -> {
                secureStorage.writeData(stringPreferencesKey(key), value.toString(), false, cipher)
            }

            KeyStoreManager.SecurityLevelType.Authenticated -> {
                secureStorage.writeData(stringPreferencesKey(key), value.toString(), true, cipher)
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override suspend fun writeBool(
        key: String,
        value: Boolean,
        secureLevel: KeyStoreManager.SecurityLevelType,
        cipher: Cipher?
    ): Boolean {
        return writeData(key, value, ::booleanPreferencesKey, secureLevel, cipher)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override suspend fun writeInt(
        key: String,
        value: Int,
        secureLevel: KeyStoreManager.SecurityLevelType,
        cipher: Cipher?
    ): Boolean {
        return writeData(key, value, ::intPreferencesKey, secureLevel, cipher)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override suspend fun writeString(
        key: String,
        value: String,
        secureLevel: KeyStoreManager.SecurityLevelType,
        cipher: Cipher?
    ): Boolean {
        return writeData(key, value, ::stringPreferencesKey, secureLevel, cipher)
    }

    private suspend fun <T> readData(
        key: String,
        keyFactory: (String) -> Preferences.Key<T>,
        secureLevel: KeyStoreManager.SecurityLevelType,
        converter: (String) -> T?,
        cipher: Cipher?
    ): T? {
         when (secureLevel) {
            KeyStoreManager.SecurityLevelType.Private -> {
                return privateStorage.readData(keyFactory(key))
            }

            KeyStoreManager.SecurityLevelType.Encrypted -> {
                return secureStorage.readData(stringPreferencesKey(key), false, cipher)?.let(converter)
            }

            KeyStoreManager.SecurityLevelType.Authenticated -> {
                return secureStorage.readData(stringPreferencesKey(key), true, cipher)?.let(converter)
            }
        }
    }

    override suspend fun readBool(
        key: String,
        secureLevel: KeyStoreManager.SecurityLevelType,
        cipher: Cipher?
    ): Boolean? {
        return readData(key, ::booleanPreferencesKey, secureLevel, String::toBoolean, cipher)
    }

    override suspend fun readInt(
        key: String,
        secureLevel: KeyStoreManager.SecurityLevelType,
        cipher: Cipher?
    ): Int? {
        return readData(key, ::intPreferencesKey, secureLevel, String::toInt, cipher)
    }

    override suspend fun readString(
        key: String,
        secureLevel: KeyStoreManager.SecurityLevelType,
        cipher: Cipher?
    ): String? {
        return readData(key, ::stringPreferencesKey, secureLevel, String::toString, cipher)
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