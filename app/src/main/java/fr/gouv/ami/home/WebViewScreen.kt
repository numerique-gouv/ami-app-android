package fr.gouv.ami.home

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.viewinterop.AndroidView
import fr.gouv.ami.api.baseUrl
import fr.gouv.ami.components.BackBar
import fr.gouv.ami.components.MainWebViewClient
import fr.gouv.ami.notifications.FirebaseService
import fr.gouv.ami.utils.ManagerLocalStorage

@Composable
fun WebViewScreen() {
    var hasBackBar by remember { mutableStateOf(false) }
    var currentUrl by remember { mutableStateOf(baseUrl) }
    var lastUrl by remember { mutableStateOf(baseUrl) } //not used for now
    var isLoading by remember { mutableStateOf(false) }

    /** UI **/

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (hasBackBar) {
                    BackBar {
                        currentUrl = baseUrl //return to home and not the latest url
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
                                        if (storage.getToken() != "") {
                                            // Post to main thread to access WebView
                                            Handler(Looper.getMainLooper()).post {
                                                FirebaseService().sendRegistration(context)
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