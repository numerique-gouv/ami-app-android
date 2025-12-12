package fr.gouv.ami.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import fr.gouv.ami.api.baseUrl
import fr.gouv.ami.components.BackBar
import fr.gouv.ami.components.MainWebViewClient
import fr.gouv.ami.notifications.FcmTokenManager
import fr.gouv.ami.ui.theme.AMITheme
import fr.gouv.ami.utils.ManagerLocalStorage
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HomeScreen() {

    val context = LocalContext.current
    var hasBackBar by remember { mutableStateOf(false) }
    var currentUrl by remember { mutableStateOf(baseUrl) }
    var lastUrl by remember { mutableStateOf(baseUrl) }
    var isLoading by remember { mutableStateOf(false) }
    val webViewRef = remember { mutableStateOf<WebView?>(null) }
    var webViewUserConnected = false

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

    /** Listen for FCM token updates **/
    LaunchedEffect(Unit) {
        FcmTokenManager.tokenUpdates.collectLatest { newToken ->
            Log.d("HomeScreen", "New FCM token received in HomeScreen: $newToken")

            // Check if user is logged in
            if (webViewUserConnected && webViewRef.value != null) {
                Log.d("HomeScreen", "User is logged in, registering device with new token")
                Handler(Looper.getMainLooper()).post {
                    triggerDeviceRegistration(webViewRef.value!!, context)
                }
            } else {
                Log.d("HomeScreen", "User not logged in or WebView not ready, skipping device registration")
            }
        }
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

            // Progress bar just above the Webview
            LinearProgressIndicator(
                modifier = Modifier.alpha(if (isLoading) 1f else 0f)
                    .fillMaxWidth()
            )

            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                factory = { it ->
                    WebView(it).apply {
                        // Store WebView reference for FCM token updates
                        webViewRef.value = this

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
                            },
                            onLoadingChanged = { isLoading = it }
                        )

                        addJavascriptInterface(object {
                            @JavascriptInterface
                            fun onEvent(eventName: String, dataJson: String) {
                                Log.d("WebView", "Event received: $eventName - $dataJson")
                                val storage = ManagerLocalStorage(context)
                                if (eventName == "user_logged_in") {
                                    webViewUserConnected = true
                                    if (storage.getToken() != "") {
                                        // Post to main thread to access WebView
                                        Handler(Looper.getMainLooper()).post {
                                            triggerDeviceRegistration(this@apply, context)
                                        }
                                    }
                                }
                            }
                        }, "NativeBridge")

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

private fun triggerDeviceRegistration(webView: WebView, context: Context) {
    val storage = ManagerLocalStorage(context)
    val fcmToken = storage.getToken() ?: ""
    val deviceId = storage.getOrCreateDeviceId()
    val deviceModel = storage.getDeviceModel()
    val appVersion = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    } catch (_: Exception) { "unknown" }

    // Escape strings for JSON
    Log.d("WebView", "Registering device: token=$fcmToken deviceId=$deviceId model=$deviceModel platform=android app_version=$appVersion")

    webView.evaluateJavascript("""
        (function() {
            console.log('Registering device...');

            fetch('${baseUrl}/api/v1/users/registrations', {
                method: 'POST',
                credentials: 'include',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({
                    subscription: {
                        fcm_token: "$fcmToken",
                        device_id: "$deviceId",
                        platform: "android",
                        app_version: "$appVersion",
                        model: "$deviceModel"
                        }
                    })
                })
                .then(response => {
                    console.log('Registration response status:', response.status);
                    return response.json();
                })
                .then(data => {
                    console.log('✅ Device registered successfully:', data);
                })
                .catch(error => {
                    console.error('❌ Registration failed:', error);
                });
            }
        )();
        """, null)
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
