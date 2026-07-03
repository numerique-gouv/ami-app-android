package fr.gouv.ami.utils.storage

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesSerializer
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class KeyStoreManager(private val context: Context) {

    enum class SecurityLevelType {
        Private,
        Encrypted,
        Authenticated
    }

    private val TAG = this::class.java.simpleName

    /*companion object {
        fun getOrCreateKey(): SecretKey {
            val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
            keyStore.getKey("datastore_key", null)?.let { return it as SecretKey }

            val keyGen = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
            )
            keyGen.init(
                KeyGenParameterSpec.Builder(
                    "datastore_key",
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()
            )
            return keyGen.generateKey()
        }
    }

    // 2. Sérialiser avec chiffrement
    object EncryptedSerializer : Serializer<Preferences> {
        override val defaultValue: Preferences = emptyPreferences()

        override suspend fun readFrom(input: InputStream): Preferences {
            val encryptedBytes = input.readBytes()
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val iv = encryptedBytes.copyOfRange(0, 12)
            val data = encryptedBytes.copyOfRange(12, encryptedBytes.size)
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(128, iv))
            val decrypted = cipher.doFinal(data)
            return PreferencesSerializer.readFrom(ByteArrayInputStream(decrypted))
        }

        override suspend fun writeTo(t: Preferences, output: OutputStream) {
            val baos = ByteArrayOutputStream()
            PreferencesSerializer.writeTo(t, baos)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
            output.write(cipher.iv)
            output.write(cipher.doFinal(baos.toByteArray()))
        }
    }

    // 3. Utiliser le DataStore chiffré — même API que le DataStore classique
    val Context.secureDataStore by dataStore(
        fileName = "secure_settings.pb",
        serializer = EncryptedSerializer
    )

    val AUTH_TOKEN = stringPreferencesKey("auth_token")

    // Écrire
    suspend fun saveToken(context: Context) {
        context.secureDataStore.edit { prefs ->
            prefs[AUTH_TOKEN] = "secret_value"
        }
    }

    // Lire
    val tokenFlow: Flow<String?> = context.secureDataStore.data
        .map { prefs -> prefs[AUTH_TOKEN] }*/
}