package fr.gouv.ami.utils.storage

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

interface CipherManager {
    @Throws(Exception::class)
    fun encrypt(inputText: String, authenticationRequired: Boolean): String

    @Throws(Exception::class)
    fun decrypt(data: String, authenticationRequired: Boolean): String
}

class CipherManagerImpl : CipherManager {
    companion object {
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val AES_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$AES_ALGORITHM/$BLOCK_MODE/$PADDING"
    }

    val encryptedKeyStoreAlias = "encrypted_key_store_alias"
    val authenticatedKeyStoreAlias = "authenticated_key_store_alias"
    private val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply {
        load(null) // With load function we initialize our keystore
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @Throws(Exception::class)
    override fun encrypt(inputText: String, authenticationRequired: Boolean): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(
            Cipher.ENCRYPT_MODE,
            getOrCreateKey(authenticationRequired)
        )

        val encryptedBytes = cipher.doFinal(inputText.toByteArray())
        val iv = cipher.iv

        val encryptedDataWithIV = ByteArray(iv.size + encryptedBytes.size)
        System.arraycopy(iv, 0, encryptedDataWithIV, 0, iv.size)
        System.arraycopy(encryptedBytes, 0, encryptedDataWithIV, iv.size, encryptedBytes.size)
        return Base64.encodeToString(encryptedDataWithIV, Base64.DEFAULT)
    }

    @Throws(Exception::class)
    override fun decrypt(data: String, authenticationRequired: Boolean): String {
        val encryptedDataWithIV = Base64.decode(data, Base64.DEFAULT)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val iv = encryptedDataWithIV.copyOfRange(0, cipher.blockSize)
        cipher.init(
            Cipher.DECRYPT_MODE, getOrCreateKey(authenticationRequired),
            IvParameterSpec(iv)
        )

        val encryptedData =
            encryptedDataWithIV.copyOfRange(cipher.blockSize, encryptedDataWithIV.size)
        val decryptedBytes = cipher.doFinal(encryptedData)
        return String(decryptedBytes, Charsets.UTF_8)
    }

    @Throws(Exception::class)
    fun createKey(authenticationRequired: Boolean): SecretKey {
        val keyStoreAlias =
            if (authenticationRequired) authenticatedKeyStoreAlias else encryptedKeyStoreAlias
        val keyGenParameterSpecBuilder = KeyGenParameterSpec.Builder(
            keyStoreAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(BLOCK_MODE)
            .setEncryptionPaddings(PADDING)
            .setUserAuthenticationRequired(authenticationRequired)
            .setRandomizedEncryptionRequired(true)
        if (authenticationRequired && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            keyGenParameterSpecBuilder.setUserAuthenticationParameters(
                0,
                KeyProperties.AUTH_BIOMETRIC_STRONG
            )
        }
        val keyGenParameterSpec = keyGenParameterSpecBuilder.build()

        return KeyGenerator.getInstance(AES_ALGORITHM).apply {
            init(keyGenParameterSpec)
        }.generateKey()
    }

    @Throws(Exception::class)
    private fun getOrCreateKey(authenticationRequired: Boolean): SecretKey {
        val alias =
            if (authenticationRequired) authenticatedKeyStoreAlias else encryptedKeyStoreAlias
        val existingKey = keyStore.getEntry(alias, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey(authenticationRequired)
    }

}