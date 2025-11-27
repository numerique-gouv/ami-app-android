package fr.gouv.ami.utils

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class ManagerLocalStorage(context: Context) {

    private val TAG = this::class.java.simpleName
    private val FIREBASE_TOKEN = "firebase_token"

    private lateinit var masterKey: MasterKey
    private lateinit var encryptedSharedPreferences: EncryptedSharedPreferences

    init {
        try {
            masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            encryptedSharedPreferences = EncryptedSharedPreferences.create(
                context,
                "token_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            ) as EncryptedSharedPreferences
        } catch (exception: Exception) {
            Log.d(TAG, exception.toString())
        }
    }

    fun saveToken(token: String) {
        encryptedSharedPreferences.edit().putString(FIREBASE_TOKEN, token)?.apply()
    }

    fun getToken(): String? {
        return encryptedSharedPreferences.getString(FIREBASE_TOKEN, null)
    }
}