package fr.gouv.ami.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import fr.gouv.ami.api.baseUrl
import fr.gouv.ami.components.BackBar
import fr.gouv.ami.components.MainWebViewClient
import fr.gouv.ami.ui.theme.AMITheme

/**
 * Helper function to get a specific cookie value from a URL
 */
fun getCookieValue(url: String, cookieName: String): String? {
    val cookieManager = CookieManager.getInstance()
    val cookieString = cookieManager.getCookie(url) ?: return null

    return cookieString.split(";")
        .map { it.trim() }
        .firstOrNull { it.startsWith("$cookieName=") }
        ?.substringAfter("=")
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HomeScreen() {

    val context = LocalContext.current
    var hasBackBar by remember { mutableStateOf(false) }
    var currentUrl by remember { mutableStateOf(baseUrl) }
    var lastUrl by remember { mutableStateOf(baseUrl) }

    /**Check notification permission **/

    val notificationPermissionGranted = remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        notificationPermissionGranted.value = isGranted
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }


    /** UI **/

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (hasBackBar) {
                BackBar {
                    currentUrl = lastUrl
                }
            }
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                factory = { it ->
                    WebView(it).apply {
                        // Enable cookie persistence, needed to stay connected to the AMI backend throughout app restarts.
                        val cookieManager = CookieManager.getInstance()
                        cookieManager.setAcceptCookie(true)
                        cookieManager.setAcceptThirdPartyCookies(this, true)

                        settings.javaScriptEnabled = true
                        settings.allowFileAccess = true
                        settings.allowContentAccess = true
                        settings.domStorageEnabled = true
                        webViewClient = MainWebViewClient(
                            baseUrl = baseUrl,
                            onBackBarChanged = { hasBackBar = it },
                            onUrlChanged = {
                                if (currentUrl.contains(baseUrl)) {
                                    lastUrl = currentUrl
                                }
                                currentUrl = it
                            }
                        )
                        loadUrl(baseUrl)
                    }
                },
                update = { webView ->
                    if (webView.url != currentUrl) {
                        webView.loadUrl(currentUrl)
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun PreviewHomeScreenLight() {
    AMITheme {
        HomeScreen()
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewHomeScreenDark() {
    AMITheme {
        HomeScreen()
    }
}
