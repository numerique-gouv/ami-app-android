package fr.gouv.ami.home

import android.util.Log
import android.webkit.JavascriptInterface
import android.content.res.Configuration
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.gouv.ami.R
import fr.gouv.ami.api.baseUrl
import fr.gouv.ami.components.BackBar
import fr.gouv.ami.components.DownloadLogsButton
import fr.gouv.ami.components.DownloadLogsViewModel
import fr.gouv.ami.components.InformationBanner
import fr.gouv.ami.components.InformationType
import fr.gouv.ami.components.MainWebViewClient
import fr.gouv.ami.global.BaseScreen
import fr.gouv.ami.notifications.FirebaseService
import fr.gouv.ami.utils.ManagerLocalStorage
import fr.gouv.ami.ui.theme.AMITheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun WebViewScreen(
    webViewViewModel: WebViewViewModel,
    goSettings: () -> Unit,
    goOnboarding: () -> Unit,
    downloadLogsViewModel: DownloadLogsViewModel = viewModel()
) {
    var hasBackBar by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val webViewRef = remember { mutableStateOf<WebView?>(null) }
    var canGoBack by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        webViewViewModel.refreshView.collect {
            //webViewRef.value?.reload() doesn't work, the history is lost
            webViewRef.value?.evaluateJavascript(
                "window.location.reload();",
                null
            )
        }
    }

    LaunchedEffect(Unit) {
        webViewViewModel.executeJavaScript.collect { script ->
            webViewRef.value?.evaluateJavascript(script, null)
            Log.d("WebView", "Executed JavaScript: $script")
        }
    }

    /** Handle back button for WebView navigation
    We can't just check webView.canGoBack() here as it would only be done during recomposition,
    which doesn't seem to happen when navigating in the webview.
    We thus need to check it in MainWebViewClient.doUpdateVisitedHistory using a callback to update
    the `canGoBack` state here.
     **/
    BackHandler(enabled = canGoBack) {
        webViewRef.value?.goBack()
    }

    /** UI **/

    val context = LocalContext.current

    BaseScreen(viewModel = webViewViewModel) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
            ) {
                if (webViewViewModel.showNotificationPermissionGrantedBanner) {
                    InformationBanner(
                        informationType = InformationType.Validation,
                        title = stringResource(R.string.notification_permission_granted),
                        icon = R.drawable.ic_information_validation,
                        onClose = { webViewViewModel.dismissNotificationPermissionGrantedBanner() }
                    )
                }

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
                                        if (it.contains("settings")) {
                                            goSettings()
                                        } else {
                                            webViewViewModel.onUrlChanged(it)
                                        }
                                    },
                                onLoadingChanged = { isLoading = it },
                                onCanGoBackChanged = { canGoBack = it },
                                onPageFinished = { webViewViewModel.notifyPageFinished() },
                            )

                            addJavascriptInterface(object {
                                @JavascriptInterface
                                fun onEvent(eventName: String, dataJson: String) {
                                    Log.d("WebView", "Event received: $eventName - $dataJson")
                                    val storage = ManagerLocalStorage(context)
                                    when (eventName) {
                                        "user_logged_in" -> {
                                            if (storage.getToken() != "") {
                                                // Post to main thread to access WebView
                                                webViewViewModel.viewModelScope.launch {
                                                    FirebaseService().sendRegistration(context)
                                                }
                                            }
                                            if (!hasRequestedPermissionBefore(context)) {
                                                webViewViewModel.viewModelScope.launch {
                                                    goOnboarding()
                                                }
                                            }
                                        }

                                        "notification_permission_requested" -> {
                                            // Trigger notification permission request
                                            webViewViewModel.viewModelScope.launch {
                                                webViewViewModel.triggerNotificationPermissionRequest()
                                            }
                                        }

                                        "notification_permission_removed" -> {
                                            // Open system settings to let user revoke permission
                                            webViewViewModel.viewModelScope.launch {
                                                webViewViewModel.openNotificationSettings()
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

            // Download logs button - appears only on contact page
            DownloadLogsButton(
                visible = webViewViewModel.isOnContactPage,
                onClick = {
                    // Fetch user_fc_hash from localStorage before sharing logs
                    webViewRef.value?.evaluateJavascript("localStorage.getItem('user_fc_hash')") { result ->
                        // Result comes as JSON string: "\"value\"" or "null"
                        val userFcHash = result
                            ?.trim()
                            ?.removeSurrounding("\"")
                            ?.takeIf { it != "null" }
                        downloadLogsViewModel.shareLogs(context, userFcHash)
                    } ?: downloadLogsViewModel.shareLogs(context)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            )
        }
    }
}

@Preview
@Composable
fun PreviewWebViewScreenLight() {
    AMITheme {
        WebViewScreen(
            webViewViewModel = viewModel(),
            goSettings = {},
            goOnboarding = {})
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewWebViewScreenDark() {
    AMITheme {
        WebViewScreen(
            webViewViewModel = viewModel(),
            goSettings = {},
            goOnboarding = {})
    }
}
