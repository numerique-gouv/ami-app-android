package fr.gouv.ami.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat.getString
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.FragmentActivity
import fr.gouv.ami.R
import fr.gouv.ami.utils.storage.CipherManagerImpl
import fr.gouv.ami.utils.storage.SecureStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.crypto.Cipher

class AmiBiometricPrompt(val context: FragmentActivity) {
    fun <T> createBioPromptWrite(
        operation: suspend (Cipher) -> T,
        onError: () -> Unit,
        onFailure: () -> Unit,
        onSuccess: (T?) -> Unit
    ) {
        val cipherManager = CipherManagerImpl()
        val cipher = cipherManager.getEncryptionCipher(true)
        createBiometricPrompt(cipher, operation, onError, onFailure, onSuccess)
    }

    //TODO separate logic and UI
    fun <T> createBioPromptRead(
        preferenceKey: String,
        operation: suspend (Cipher) -> T,
        secureStorage: SecureStorage,
        onError: () -> Unit,
        onFailure: () -> Unit,
        onSuccess: (T?) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val data = secureStorage.store(true).data.map { preferences ->
                preferences[stringPreferencesKey(preferenceKey)]
            }
                .first()

            val cipherManager = CipherManagerImpl()
            val cipher = cipherManager.getDecryptionCipher(data ?: "", true)
            createBiometricPrompt(cipher, operation, onError, onFailure, onSuccess)
        }
    }

    private fun <T> createBiometricPrompt(
        cipher: Cipher,
        operation: suspend (Cipher) -> T,
        onError: () -> Unit,
        onFailure: () -> Unit,
        onSuccess: (T?) -> Unit
    ) {
        val cryptoObject = BiometricPrompt.CryptoObject(cipher)
        val biometricPrompt = createPromptInfo()
        BiometricPrompt(
            context, createCallbackObject(
                operation = operation,
                onError = onError,
                onFailure = onFailure,
                onSuccess = onSuccess
            )
        )
            .authenticate(biometricPrompt, cryptoObject)
    }

    fun createPromptInfo(): BiometricPrompt.PromptInfo =
        BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(context, R.string.biometric_prompt_title))
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .setConfirmationRequired(false)
            .setNegativeButtonText(getString(context, R.string.commun_cancel))
            .build()

    private fun <T> createCallbackObject(
        operation: suspend (Cipher) -> T,
        onError: () -> Unit,
        onFailure: () -> Unit,
        onSuccess: (T?) -> Unit
    ): BiometricPrompt.AuthenticationCallback =
        object : BiometricPrompt.AuthenticationCallback() {

            // User clicks negative button or prompt is cancelled in any other way
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError()
            }

            // Biometrics provided does not match device's
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onFailure()
            }

            // Biometrics match
            @RequiresApi(Build.VERSION_CODES.P)
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                try {
                    val cipher = result.cryptoObject!!.cipher!!
                    CoroutineScope(Dispatchers.Main).launch {
                        val value = operation(cipher)
                        onSuccess(value)
                    }
                } catch (error: Throwable) {
                    onError()
                }
            }
        }

}