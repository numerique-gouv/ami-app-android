package fr.gouv.ami

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import fr.gouv.ami.utils.storage.PrivateStorage
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import fr.gouv.ami.utils.Result
import fr.gouv.ami.utils.storage.DataStoreFactory
import fr.gouv.ami.utils.storage.LocalStorageError
import org.junit.BeforeClass


@RunWith(AndroidJUnit4::class)
class PrivateStorageTest {

    companion object {
        private lateinit var storage: PrivateStorage

        @BeforeClass
        @JvmStatic
        fun setup() {
            val context = ApplicationProvider.getApplicationContext<Context>()

            storage = PrivateStorage(
                DataStoreFactory.create(context, "test")
            )
        }
    }

    @Test
    fun writeAndReadString() = runTest {

        storage.writeData(
            stringPreferencesKey("name"),
            "Alice"
        )

        val result = storage.readData(
            stringPreferencesKey("name")
        )

        assertEquals(
            Result.Success("Alice"),
            result
        )
    }

    @Test
    fun writeAndReadInt() = runTest {

        storage.writeData(
            intPreferencesKey("age"),
            23
        )

        val result = storage.readData(
            stringPreferencesKey("age")
        )

        assertEquals(
            Result.Success(23),
            result
        )
    }

    @Test
    fun writeAndReadBool() = runTest {

        storage.writeData(
            booleanPreferencesKey("isActive"),
            true
        )

        val result = storage.readData(
            stringPreferencesKey("isActive")
        )

        assertEquals(
            Result.Success(true),
            result
        )
    }

    @Test
    fun writeAndCleanValue() = runTest {
        val key = stringPreferencesKey("empty")

        storage.writeData(
            key,
            "Value"
        )

        storage.deleteData(
            key
        )

        val result = storage.readData(key)

        assertEquals(
            Result.Failure(LocalStorageError.KeyNotFound),
            result
        )
    }

    @Test
    fun readMissingKeyReturnsError() = runTest {

        val result = storage.readData(
            stringPreferencesKey("unknown")
        )

        assertEquals(
            Result.Failure(LocalStorageError.KeyNotFound),
            result
        )
    }


}