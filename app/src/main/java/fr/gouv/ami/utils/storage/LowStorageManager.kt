package fr.gouv.ami.utils.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class LowStorageManager(private val context: Context) {
    private val TAG = this::class.java.simpleName

    companion object {
        private val STORAGE_NAME = "low_storage"
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = STORAGE_NAME)
    }

    private val FIREBASE_TOKEN = stringPreferencesKey("firebase_token")
    
    private val BEARER_TOKEN = stringPreferencesKey("bearer_token")

    suspend fun saveFirebaseToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[FIREBASE_TOKEN] = token
        }
    }

    val firebaseToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[FIREBASE_TOKEN]
    }

    suspend fun getFirebaseToken(): String? =
        firebaseToken.first()

    suspend fun saveBearer(token: String) {
        context.dataStore.edit { preferences ->
            preferences[BEARER_TOKEN] = token
        }
    }

    val bearerToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[BEARER_TOKEN]
    }

    suspend fun getBearerToken(): String? =
        bearerToken.first()

    suspend fun clearBearer() {
        context.dataStore.edit { preferences ->
            preferences.remove(BEARER_TOKEN)
        }
    }
}