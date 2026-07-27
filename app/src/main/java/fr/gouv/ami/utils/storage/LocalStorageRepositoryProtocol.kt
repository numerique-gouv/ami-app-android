package fr.gouv.ami.utils.storage

import fr.gouv.ami.utils.Result
import javax.crypto.Cipher

abstract class LocalStorageRepositoryProtocol() {

    abstract suspend fun writeBool(key: String, value: Boolean, secureLevel: KeyStoreManager.SecurityLevelType, cipher: Cipher? = null): Result<Unit, LocalStorageError>
    abstract suspend fun writeInt(key: String, value: Int, secureLevel: KeyStoreManager.SecurityLevelType, cipher: Cipher? = null): Result<Unit, LocalStorageError>
    abstract suspend fun writeString(key: String, value: String, secureLevel: KeyStoreManager.SecurityLevelType, cipher: Cipher? = null): Result<Unit, LocalStorageError>
    abstract suspend fun readBool(key: String, secureLevel: KeyStoreManager.SecurityLevelType, cipher: Cipher? = null): Result<Boolean, LocalStorageError>
    abstract suspend fun readInt(key: String, secureLevel: KeyStoreManager.SecurityLevelType, cipher: Cipher? = null): Result<Int, LocalStorageError>
    abstract suspend fun readString(key: String, secureLevel: KeyStoreManager.SecurityLevelType, cipher: Cipher? = null): Result<String, LocalStorageError>
    abstract suspend fun deleteBool(key: String, secureLevel: KeyStoreManager.SecurityLevelType)
    abstract suspend fun deleteInt(key: String, secureLevel: KeyStoreManager.SecurityLevelType)
    abstract suspend fun deleteString(key: String, secureLevel: KeyStoreManager.SecurityLevelType)
    abstract suspend fun deleteAll(secureLevel: KeyStoreManager.SecurityLevelType)

}