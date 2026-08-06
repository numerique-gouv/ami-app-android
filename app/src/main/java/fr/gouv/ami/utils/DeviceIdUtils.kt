package fr.gouv.ami.utils

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import fr.gouv.ami.utils.storage.DataStoreFactory
import fr.gouv.ami.utils.storage.PrivateStorage
import java.util.UUID

class DeviceIdUtils(val context: Context) {

    private val TAG = this::class.java.simpleName
    private val DEVICE_ID_KEY = stringPreferencesKey("device_id")
    private val SETTING_STORAGE_KEY = "settings"
    private val privateStorage =
        PrivateStorage(DataStoreFactory.get(context, SETTING_STORAGE_KEY))

    suspend fun getOrCreateDeviceId(): String {
        val deviceIdResult = privateStorage.readData(DEVICE_ID_KEY)
        val deviceId: String =
            when (deviceIdResult) {
                is Result.Success -> {
                    deviceIdResult.value
                }

                is Result.Failure -> {
                    createDeviceId()
                }
            }
        Log.d(TAG, "the deviceId is $deviceId")
        return deviceId
    }

    private suspend fun createDeviceId(): String {
        val deviceId = UUID.randomUUID().toString()
        privateStorage.writeData(DEVICE_ID_KEY, deviceId)
        return deviceId
    }
}