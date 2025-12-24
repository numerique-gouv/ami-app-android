package fr.gouv.ami

import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import fr.gouv.ami.ui.theme.AMITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Initialize CookieManager before creating WebView to ensure cookies are properly restored
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)

        // Log existing cookies on startup to verify persistence
        val cookies = cookieManager.getCookie(fr.gouv.ami.api.baseUrl)
        Log.d("CookiePersistence", "Cookies on app start: $cookies")

        enableEdgeToEdge()
        setContent {
            AMITheme {
                HomeApp()
            }
        }
    }
}
