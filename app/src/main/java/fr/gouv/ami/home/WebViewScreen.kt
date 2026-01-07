package fr.gouv.ami.home

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.JavascriptInterface
import android.content.res.Configuration
import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.gouv.ami.api.baseUrl
import fr.gouv.ami.components.BackBar
import fr.gouv.ami.components.MainWebViewClient
import fr.gouv.ami.global.BaseScreen
import fr.gouv.ami.notifications.FirebaseService
import fr.gouv.ami.utils.ManagerLocalStorage
import fr.gouv.ami.ui.theme.AMITheme

@Composable
fun WebViewScreen(webViewViewModel: WebViewViewModel) {
    var hasBackBar by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val webViewRef = remember { mutableStateOf<WebView?>(null) }

    LaunchedEffect(Unit) {
        webViewViewModel.refreshView.collect {
            //webViewRef.value?.reload() doesn't work, the history is lost
            webViewRef.value?.evaluateJavascript(
                "window.location.reload();",
                null
            )
        }
    }

    /** UI **/

    BaseScreen(webViewViewModel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (hasBackBar) {
                BackBar {
                    webViewViewModel.onBackPressed()
                }
            }

            // Progress bar just above the Webview
            LinearProgressIndicator(
                modifier = Modifier
                    .alpha(if (isLoading) 1f else 0f)
                    .fillMaxWidth()
            )

            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                factory = { it ->
                    WebView(it).apply {
                        webViewRef.value = this

                        settings.javaScriptEnabled = true
                        settings.allowFileAccess = true
                        settings.allowContentAccess = true
                        settings.domStorageEnabled = true
                        webViewClient = MainWebViewClient(
                            baseUrl = baseUrl,
                            onBackBarChanged = { hasBackBar = it },
                            onUrlChanged =
                                {
                                    webViewViewModel.onUrlChanged(it)
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
                        loadUrl(webViewViewModel.currentUrl)
                    }
                },
                update = { webView ->
                    if (webView.url != webViewViewModel.currentUrl) {
                        webView.loadUrl(webViewViewModel.currentUrl)
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun PreviewWebViewScreenLight() {
    AMITheme {
        WebViewScreen(viewModel())
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewWebViewScreenDark() {
    AMITheme {
        WebViewScreen(viewModel())
    }
}