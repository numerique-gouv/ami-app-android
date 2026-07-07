package fr.gouv.ami.home

import android.content.Context
import android.util.Log
import fr.gouv.ami.BuildConfig
import fr.gouv.ami.R
import fr.gouv.ami.utils.ManagerLocalStorage

class WebviewScripts {

    enum class EventWebview(val value: String) {
        USER_LOGGED_IN("user_logged_in"),
        USER_LOGGED_OUT("user_logged_out"),
        NOTIFICATION_PERMISSION_REQUESTED("notification_permission_requested"),
        NOTIFICATION_PERMISSION_REMOVED("notification_permission_removed");


        companion object {
            fun fromValue(value: String): EventWebview? =
                entries.find { it.value == value }
        }
    }

    companion object {
        val TAG = this::class.java.simpleName

        fun nativeInfosScript(
            context: Context
        ): String {
            val managerStorage = ManagerLocalStorage(context)
            val deviceId = managerStorage.getOrCreateDeviceId()
            Log.d(TAG, "device_id sending in nativeInfosScript is $deviceId")

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
                    device_id: "$deviceId"
                };
            };
            
            window.NativeInfos.displayBanner = function() {

                const banner = document.createElement('native-info-banner');
                banner.innerText = JSON.stringify(
                    window.NativeInfos.getInfos()
                );

                banner.style.cssText = `
                    position: fixed;
                    top: 0;
                    left: 0;
                    width: 100%;
                    padding: 12px 16px;
                    background-color: #007AFF;
                    color: white;
                    font-size: 16px;
                    font-family: sans-serif;
                    font-weight: 600;
                    z-index: 999999;
                    box-sizing: border-box;
                    text-align: center;
                `;
                
                console.log("body =", document.body);

                document.body?.insertBefore(
                    banner,
                    document.body.firstChild
                );
            };

            console.log('NativeInfos initialized');
            
            if (document.body) {
                window.NativeInfos.displayBanner();
            } else {
                document.addEventListener(
                    'DOMContentLoaded',
                    window.NativeInfos.displayBanner,
                    { once: true }
                );
            }
        })();
    """.trimIndent()
        }
    }
}