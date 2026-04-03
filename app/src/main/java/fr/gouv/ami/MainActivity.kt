package fr.gouv.ami

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import fr.gouv.ami.api.baseUrl
import fr.gouv.ami.home.WebViewViewModel
import fr.gouv.ami.notifications.FirebaseService
import fr.gouv.ami.notifications.TokenManager
import fr.gouv.ami.ui.theme.AMITheme

class MainActivity : ComponentActivity() {
    private val TAG = this::class.java.simpleName

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
}
