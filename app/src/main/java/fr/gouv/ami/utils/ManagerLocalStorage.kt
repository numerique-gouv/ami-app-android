package fr.gouv.ami.utils

import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import android.os.Build

class ManagerLocalStorage(private val context: Context) {

    private val TAG = this::class.java.simpleName
    private val FIREBASE_TOKEN = "firebase_token"
    private val DEVICE_ID = "device_id"

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

    /**
     * Gets or creates a unique device ID based on Android ID.
     * The Android ID is unique per device + app combination and persists across app updates.
     */
    fun getOrCreateDeviceId(): String {
        var deviceId = encryptedSharedPreferences.getString(DEVICE_ID, null)
        if (deviceId == null) {
            // Use Android ID
            deviceId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            encryptedSharedPreferences.edit().putString(DEVICE_ID, deviceId)?.apply()
            Log.d(TAG, "Using Android ID as device ID: $deviceId")
        }
        return deviceId
    }

    /**
     * Returns the device model for display/debugging purposes.
     * Example: "Samsung SM-G991B"
     */
    fun getDeviceModel(): String {
        return "${Build.MANUFACTURER} ${Build.MODEL}"
    }
}