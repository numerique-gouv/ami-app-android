package fr.gouv.ami

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import fr.gouv.ami.components.AmiBiometricPrompt
import fr.gouv.ami.utils.storage.DataStoreFactory
import fr.gouv.ami.utils.storage.LocalStorageError
import fr.gouv.ami.utils.storage.SecureStorage
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test
import fr.gouv.ami.utils.Result
import fr.gouv.ami.utils.storage.CipherManagerImpl
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class SecureStorageTest {

    companion object {
        private lateinit var secureStorage: SecureStorage

        @BeforeClass
        @JvmStatic
        fun setup() {
            val context = ApplicationProvider.getApplicationContext<Context>()

            secureStorage = SecureStorage(
                DataStoreFactory.create(context, "test_encrypted"),
                DataStoreFactory.create(context, "test_authenticated")
            )
        }
    }

    @Test
    fun writeAndReadString() = runTest {

        secureStorage.writeData(
            stringPreferencesKey("name"),
            "Alice",
            false,
            null
        )

        val result = secureStorage.readData(
            stringPreferencesKey("name"),
            false,
            null
        )

        assertEquals(
            Result.Success("Alice"),
            result
        )
    }

    //returns true if a value deleted from authenticated storage is not deleted from the encrypted store
    @Test
    fun deleteInEncryptedStorage() = runTest {
        val key = "nameCrypted"
        val name = "Alice"
        secureStorage.writeData(
            stringPreferencesKey(key),
            name,
            false,
            null
        )

        secureStorage.deleteAll(true)

        val result = secureStorage.readData(stringPreferencesKey(key), false, null)

        assertEquals(Result.Success(name), result)
    }

    @Test
    fun encryptAndDecryptWithinBiometric() = runTest {
        val cipherManager = CipherManagerImpl()
        val valueCrypted = cipherManager.encrypt("encryptAndDecrypt", false, null)
        val valueDecrypted = cipherManager.decrypt(valueCrypted, false, null)

        assertEquals("encryptAndDecrypt", valueDecrypted)
    }

    @Test
    fun readMissingKeyReturnsError() = runTest {

        val result = secureStorage.readData(
            stringPreferencesKey("unknown"),
            true,
            null
        )

        assertEquals(
            Result.Failure(LocalStorageError.KeyNotFound),
            result
        )
    }

    @Test
    fun writeAndReadAuthenticatedString() = runTest {

        val activity = ActivityScenario.launch(MainActivity::class.java)

        val latch = CountDownLatch(1)

        var result: Result<Boolean, LocalStorageError>? = null

        val key = "token"
        val savedValue = "saved value"

        //Write value
        activity.onActivity { currentActivity ->

            val biometricPrompt = AmiBiometricPrompt(currentActivity)

            biometricPrompt.createBioPromptWrite(

                operation = { cipher ->

                    secureStorage.writeData(
                        preferenceKey = stringPreferencesKey(key),
                        value = savedValue,
                        authenticationRequired = true,
                        cipher = cipher
                    )

                },
                onError = { result = Result.Failure(LocalStorageError.AuthenticationFailed) },
                onSuccess = { result = Result.Success(true) },
                onFailure = { result = Result.Failure(LocalStorageError.AuthenticationFailed) }
            )
        }

        // Le test attend que l'utilisateur valide le prompt
        latch.await(10, TimeUnit.SECONDS)

        assertEquals(
            Result.Success(true),
            result
        )

        // Read value
        latch.await(2, TimeUnit.SECONDS)
        var resultRead: Result<String, LocalStorageError>? = null
        activity.onActivity { currentActivity ->

            val biometricPrompt = AmiBiometricPrompt(currentActivity)

            biometricPrompt.createBioPromptRead(
                preferenceKey = key,
                operation = { cipher ->
                    secureStorage.readData(
                        preferenceKey = stringPreferencesKey(key),
                        authenticationRequired = true,
                        cipher = cipher
                    )

                },
                secureStorage = secureStorage,
                onError = { resultRead = Result.Failure(LocalStorageError.AuthenticationFailed) },
                onSuccess = { value ->
                    resultRead = value
                },
                onFailure = { resultRead = Result.Failure(LocalStorageError.AuthenticationFailed) }
            )
        }


        // Le test attend que l'utilisateur valide le prompt
        latch.await(10, TimeUnit.SECONDS)
        assertEquals(
            Result.Success(savedValue),
            resultRead
        )
    }
}