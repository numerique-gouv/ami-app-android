package fr.gouv.ami.utils.storage

abstract class LocalStorageRepositoryProtocol() {

    abstract suspend fun writeBool(key: String, value: Boolean, secureLevel: KeyStoreManager.SecurityLevelType): Boolean
    abstract suspend fun writeInt(key: String, value: Int, secureLevel: KeyStoreManager.SecurityLevelType): Boolean
    abstract suspend fun writeString(key: String, value: String, secureLevel: KeyStoreManager.SecurityLevelType): Boolean
    abstract suspend fun readBool(key: String, secureLevel: KeyStoreManager.SecurityLevelType): Boolean?
    abstract suspend fun readInt(key: String, secureLevel: KeyStoreManager.SecurityLevelType): Int?
    abstract suspend fun readString(key: String, secureLevel: KeyStoreManager.SecurityLevelType): String?
    abstract suspend fun deleteBool(key: String, secureLevel: KeyStoreManager.SecurityLevelType): Boolean
    abstract suspend fun deleteInt(key: String, secureLevel: KeyStoreManager.SecurityLevelType): Boolean
    abstract suspend fun deleteString(key: String, secureLevel: KeyStoreManager.SecurityLevelType): Boolean
    abstract suspend fun deleteAll(secureLevel: KeyStoreManager.SecurityLevelType): Boolean

}