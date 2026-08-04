package fr.gouv.ami.utils.storage

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import fr.gouv.ami.utils.Result
import javax.crypto.Cipher

class ILocalStorageRepository(val context: Context) : LocalStorageRepositoryProtocol() {
    private var privateStorage: PrivateStorage
    private var secureStorage: SecureStorage

    init {
        val userStoreId = "tmp" //TODO à changer
        privateStorage = PrivateStorage(
            DataStoreFactory.create(context, userStoreId)
        )
        secureStorage = SecureStorage(
            DataStoreFactory.create(context, "${userStoreId}_encrypted"),
            DataStoreFactory.create(context, "${userStoreId}_authenticated")
        )
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private suspend fun <T> writeData(
        key: String,
        value: T,
        keyFactory: (String) -> Preferences.Key<T>,
        secureLevel: KeyStoreManager.SecurityLevelType,
        cipher: Cipher?,
    ): Result<Unit, LocalStorageError> {
        try {
            when (secureLevel) {
                KeyStoreManager.SecurityLevelType.Private -> {
                    privateStorage.writeData(keyFactory(key), value)
                }

                KeyStoreManager.SecurityLevelType.Encrypted -> {
                    secureStorage.writeData(
                        stringPreferencesKey(key),
                        value.toString(),
                        false,
                        cipher
                    )
                }

                KeyStoreManager.SecurityLevelType.Authenticated -> {
                    secureStorage.writeData(
                        stringPreferencesKey(key),
                        value.toString(),
                        true,
                        cipher
                    )
                }
            }
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Failure(LocalStorageError(e))
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override suspend fun writeBool(
        key: String,
        value: Boolean,
        secureLevel: KeyStoreManager.SecurityLevelType,
        cipher: Cipher?
    ): Result<Unit, LocalStorageError> {
        return writeData(key, value, ::booleanPreferencesKey, secureLevel, cipher)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override suspend fun writeInt(
        key: String,
        value: Int,
        secureLevel: KeyStoreManager.SecurityLevelType,
        cipher: Cipher?
    ): Result<Unit, LocalStorageError> {
        return writeData(key, value, ::intPreferencesKey, secureLevel, cipher)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override suspend fun writeString(
        key: String,
        value: String,
        secureLevel: KeyStoreManager.SecurityLevelType,
        cipher: Cipher?
    ): Result<Unit, LocalStorageError> {
        return writeData(key, value, ::stringPreferencesKey, secureLevel, cipher)
    }

    private suspend fun <T> readData(
        key: String,
        keyFactory: (String) -> Preferences.Key<T>,
        secureLevel: KeyStoreManager.SecurityLevelType,
        converter: (String) -> T?,
        cipher: Cipher?
    ): Result<T, LocalStorageError> {
        when (secureLevel) {
            KeyStoreManager.SecurityLevelType.Private -> {
                return privateStorage.readData(keyFactory(key))
            }

            KeyStoreManager.SecurityLevelType.Encrypted -> {
                val result = secureStorage.readData(
                    stringPreferencesKey(key),
                    false,
                    cipher
                )
                return mapResult(result, converter)
            }

            KeyStoreManager.SecurityLevelType.Authenticated -> {

                val result = secureStorage.readData(
                    stringPreferencesKey(key),
                    true,
                    cipher
                )
                return mapResult(result, converter)
            }
        }
    }

    private fun <T> mapResult(
        result: Result<String, LocalStorageError>,
        converter: (String) -> T?
    ): Result<T, LocalStorageError> {
        return when (
            result
        ) {
            is Result.Success -> {
                val converted = converter(result.value)

                if (converted != null) {
                    Result.Success(converted)
                } else {
                    Result.Failure(LocalStorageError.DecryptionFailed)
                }
            }

            is Result.Failure -> result
        }
    }

    override suspend fun readBool(
        key: String,
        secureLevel: KeyStoreManager.SecurityLevelType,
        cipher: Cipher?
    ): Result<Boolean, LocalStorageError> {
        return readData(key, ::booleanPreferencesKey, secureLevel, String::toBoolean, cipher)
    }

    override suspend fun readInt(
        key: String,
        secureLevel: KeyStoreManager.SecurityLevelType,
        cipher: Cipher?
    ): Result<Int, LocalStorageError> {
        return readData(key, ::intPreferencesKey, secureLevel, String::toInt, cipher)
    }

    override suspend fun readString(
        key: String,
        secureLevel: KeyStoreManager.SecurityLevelType,
        cipher: Cipher?
    ): Result<String, LocalStorageError> {
        return readData(key, ::stringPreferencesKey, secureLevel, String::toString, cipher)
    }

    private suspend fun <T> delete(
        key: String,
        keyFactory: (String) -> Preferences.Key<T>,
        secureLevel: KeyStoreManager.SecurityLevelType
    ) {
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
    }

    override suspend fun deleteBool(
        key: String,
        secureLevel: KeyStoreManager.SecurityLevelType
    ) {
        delete(key, ::booleanPreferencesKey, secureLevel)
    }

    override suspend fun deleteInt(
        key: String,
        secureLevel: KeyStoreManager.SecurityLevelType
    ) {
        delete(key, ::intPreferencesKey, secureLevel)
    }

    override suspend fun deleteString(
        key: String,
        secureLevel: KeyStoreManager.SecurityLevelType
    ) {
        delete(key, ::stringPreferencesKey, secureLevel)
    }

    override suspend fun deleteAll(secureLevel: KeyStoreManager.SecurityLevelType) {
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
    }
}