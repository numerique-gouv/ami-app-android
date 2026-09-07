package fr.gouv.ami

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient.FileChooserParams
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import fr.gouv.ami.api.baseUrl
import fr.gouv.ami.home.WebViewViewModel
import fr.gouv.ami.notifications.FirebaseService
import fr.gouv.ami.notifications.TokenManager
import fr.gouv.ami.ui.theme.AMITheme

class MainActivity : FragmentActivity() {
    private val TAG = this::class.java.simpleName

    //launcher for file chooser
    var filePathCallback: ValueCallback<Array<Uri>>? = null
    var fileChooserParams: FileChooserParams? = null
    val filePickerLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == RESULT_OK) {
                val data = result.data

                val uris = if (data?.clipData != null) {
                    Array(data.clipData!!.itemCount) { index ->
                        data.clipData!!.getItemAt(index).uri
                    }
                } else if (data?.data != null) {
                    arrayOf(data.data!!)
                } else {
                    null
                }

                filePathCallback?.onReceiveValue(uris)
            } else {
                filePathCallback?.onReceiveValue(null)
            }
        }

    //launcher for camera
    var cameraImageUri: Uri? = null
    val cameraLauncher =
        registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success ->

            val callback = filePathCallback
            filePathCallback = null

            if (success && cameraImageUri != null) {
                callback?.onReceiveValue(
                    arrayOf(cameraImageUri!!)
                )
            } else {
                callback?.onReceiveValue(null)
            }

            cameraImageUri = null
        }

    //permission launcher
    var onPermissionResult: ((Boolean) -> Unit)? = null
    val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            onPermissionResult?.invoke(granted)
            onPermissionResult = null
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Initialize CookieManager before creating WebView to ensure cookies are properly restored
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        TokenManager().saveFirebaseToken(applicationContext)

        val url = extractBaseUrl(intent)
        Log.d(TAG, "onCreate: baseUrl from the intent: $url")
        if (url != null) {
            baseUrl = url
        }

        enableEdgeToEdge()
        setContent {
            AMITheme {
                HomeApp(pendingUrl = if (url != null) "${url}#/notifications" else null)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val url = extractBaseUrl(intent) ?: return
        Log.d(TAG, "onNewIntent: baseUrl from the intent: $url")
        baseUrl = url
        ViewModelProvider(this)[WebViewViewModel::class.java].currentUrl = "${url}#/notifications"
    }

    private fun extractBaseUrl(intent: Intent): String? {
        intent.extras?.keySet()?.forEach { key ->
            Log.d(TAG, "intent extra: $key = ${intent.extras?.get(key)}")
        }
        val public_app_url = intent.getStringExtra(FirebaseService.APP_URL)
        Log.d(TAG, "APP_URL from intent extras: $public_app_url")
        if (public_app_url != null) {
            return public_app_url
        }
        if (intent.hasExtra(FirebaseService.NOTIFICATION_TITLE)) {
            // Before PR #725 the backend didn't send the APP_URL in the notification data payload.
            // Still continue as if it was provided, but with the base url, as a fallback.
            return baseUrl
        }
        return null
    }

    fun cancelFileChooser() {
        filePathCallback?.onReceiveValue(null)

        filePathCallback = null
        fileChooserParams = null
    }
}
