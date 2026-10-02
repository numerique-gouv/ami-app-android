package fr.gouv.ami.home

import android.content.Context
import android.util.Log
import fr.gouv.ami.BuildConfig
import fr.gouv.ami.R
import fr.gouv.ami.navigation.PromotedUrls
import fr.gouv.ami.utils.DeviceIdUtils
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WebviewScripts {

    enum class EventWebview(val value: String) {
        USER_LOGGED_IN("user_logged_in"),
        USER_LOGGED_OUT("user_logged_out"),
        NOTIFICATION_PERMISSION_REQUESTED("notification_permission_requested"),
        NOTIFICATION_PERMISSION_REMOVED("notification_permission_removed"),
        WEBAPP_BRIDGE_READY("webappBridgeReady");


        companion object {
            fun fromValue(value: String): EventWebview? =
                entries.find { it.value == value }
        }
    }

    companion object {
        val TAG = this::class.java.simpleName

        suspend fun nativeInfosScript(
            context: Context
        ): String {
            val deviceId = DeviceIdUtils(context).getOrCreateDeviceId()
            Log.d(TAG, "device_id sending in nativeInfosScript is $deviceId")
            val urls = Json.encodeToString(PromotedUrls.promoted)

            return """
        (function() {
            window.NativeInfos = window.NativeInfos || {};

            window.NativeInfos.getInfos = function() {
                return {
                    platform: "android",
                    app_name: "${context.getString(R.string.app_name)}",
                    version: "${BuildConfig.VERSION_NAME}",
                    build: ${BuildConfig.VERSION_CODE},
                    environment: "${BuildConfig.FLAVOR}",
                    mode: "${BuildConfig.BUILD_TYPE}",
                    device_id: "$deviceId",
                    promoted_url_aliases: ${Json.encodeToString(urls)}
                };
            };
        })();
    """.trimIndent()
        }
    }
}