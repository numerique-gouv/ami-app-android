package fr.gouv.ami.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import fr.gouv.ami.MainActivity
import fr.gouv.ami.R
import fr.gouv.ami.api.baseUrl
import fr.gouv.ami.components.BackBar
import fr.gouv.ami.components.DownloadLogsButton
import fr.gouv.ami.components.DownloadLogsViewModel
import fr.gouv.ami.components.ImportFileBottomSheet
import fr.gouv.ami.components.InformationBanner
import fr.gouv.ami.components.InformationType
import fr.gouv.ami.components.PrimaryButton
import fr.gouv.ami.components.SecondaryButton
import fr.gouv.ami.components.webviewClient.MainWebChromeClient
import fr.gouv.ami.components.webviewClient.MainWebViewClient
import fr.gouv.ami.global.BaseScreen
import fr.gouv.ami.global.PermissionManager
import fr.gouv.ami.home.WebviewScripts.Companion.nativeInfosScript
import fr.gouv.ami.home.WebviewScripts.EventWebview
import fr.gouv.ami.notifications.FirebaseService
import fr.gouv.ami.ui.theme.AMITheme
import fr.gouv.ami.utils.FileUtils
import fr.gouv.ami.utils.storage.LowStorageManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(
    webViewViewModel: WebViewViewModel,
    goSettings: () -> Unit,
    goAuth: () -> Unit,
    goOnboarding: () -> Unit,
    downloadLogsViewModel: DownloadLogsViewModel = viewModel(),
    startUrl: String = baseUrl
) {
    val TAG = "WebViewScreen"
    var hasBackBar by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val webViewRef = remember { mutableStateOf<WebView?>(null) }
    var canGoBack by remember { mutableStateOf(false) }
    val swipeRefreshRef = remember { mutableStateOf<SwipeRefreshLayout?>(null) }
    val activity = LocalContext.current as MainActivity

    LaunchedEffect(Unit) {
        webViewViewModel.currentUrl = startUrl
    }

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
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showPermissionAlert by remember { mutableStateOf(false) }

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

                if (webViewViewModel.showSSLErrorBanner) {
                    InformationBanner(
                        informationType = InformationType.Error,
                        title = stringResource(R.string.ssl_error_title),
                        icon = R.drawable.ic_information_error,
                        content = stringResource(R.string.ssl_error_description),
                        onClose = { webViewViewModel.dismissSSLErrorBanner() }
                    )
                }

                if (hasBackBar) {
                    BackBar {
                        (context as Activity).onBackPressed()
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
                        swipeRefreshRef.value = SwipeRefreshLayout(context)
                        WebView(it).apply {
                            webViewRef.value = this

                            settings.javaScriptEnabled = true
                            settings.allowFileAccess = true
                            settings.allowContentAccess = true
                            settings.domStorageEnabled = true
                            Log.d(TAG, "Creating MainWebViewClient with baseURL ${baseUrl}")
                            webChromeClient = MainWebChromeClient(
                                activity = activity,
                                visibilityModalFilesChanged = { showBottomSheet = true })
                            webViewClient = MainWebViewClient(
                                baseUrl = baseUrl,
                                onBackBarChanged = { hasBackBar = it },
                                onUrlChanged =
                                    {
                                        if (it.endsWith("#/preferences/notifications") || it.endsWith(
                                                "#/settings"
                                            )
                                        ) {
                                            goSettings()
                                        } else {
                                            webViewViewModel.onUrlChanged(it)
                                        }
                                    },
                                onLoadingChanged = { isLoading = it },
                                onCanGoBackChanged = { canGoBack = it },
                                onPageFinished = {
                                    webViewViewModel.notifyPageFinished()
                                },
                                onSslError = { webViewViewModel.showSSLErrorBanner() },
                            )

                            setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
                                FileUtils(context).downloadFile(
                                    url.toUri(),
                                    contentDisposition,
                                    mimeType
                                )
                            }

                            if (
                                WebViewFeature.isFeatureSupported(
                                    WebViewFeature.DOCUMENT_START_SCRIPT
                                )
                            ) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    WebViewCompat.addDocumentStartJavaScript(
                                        webViewRef.value!!,
                                        nativeInfosScript(context),
                                        setOf("*")
                                    )

                                    withContext(Dispatchers.Main) {
                                        loadUrl(webViewViewModel.currentUrl)
                                    }
                                }
                            } else {
                                // No need to wait for Coroutine to load NativeInfos JS.
                                // Call loadURL as before. We are on Main UI Thread.
                                loadUrl(webViewViewModel.currentUrl)
                            }

                            addJavascriptInterface(object {
                                @JavascriptInterface
                                fun onEvent(eventName: String, dataJson: String) {
                                    Log.d("WebView", "Event received: $eventName - $dataJson")
                                    val storage = LowStorageManager(context)
                                    val event = EventWebview.fromValue(eventName)
                                    when (event) {
                                        EventWebview.USER_LOGGED_IN -> {
                                            // Post to main thread to access WebView
                                            webViewViewModel.viewModelScope.launch {
                                                val bearerToken = storage.bearerToken.first()
                                                if (!bearerToken.isNullOrEmpty()) {
                                                    FirebaseService().sendRegistration(bearerToken)
                                                }
                                            }
                                            if (!hasRequestedPermissionBefore(context)) {
                                                webViewViewModel.viewModelScope.launch {
                                                    goOnboarding()
                                                }
                                            }
                                        }

                                        EventWebview.USER_LOGGED_OUT -> {
                                            webViewViewModel.viewModelScope.launch {
                                                storage.clearBearer()
                                                goAuth()
                                            }
                                        }

                                        EventWebview.NOTIFICATION_PERMISSION_REQUESTED -> {
                                            // Trigger notification permission request
                                            webViewViewModel.viewModelScope.launch {
                                                webViewViewModel.triggerNotificationPermissionRequest()
                                            }
                                        }

                                        EventWebview.NOTIFICATION_PERMISSION_REMOVED -> {
                                            // Open system settings to let user revoke permission
                                            webViewViewModel.viewModelScope.launch {
                                                webViewViewModel.openNotificationSettings()
                                            }
                                        }

                                        else -> {}
                                    }
                                }
                            }, "NativeBridge")
                        }
                        swipeRefreshRef.value?.addView(webViewRef.value)

                        swipeRefreshRef.value?.setOnRefreshListener {
                            webViewViewModel.requestRefresh()
                        }

                        swipeRefreshRef.value!!
                    },
                    update = { _ ->
                        swipeRefreshRef.value?.isRefreshing = webViewViewModel.isRefreshing

                        // allow passkey if it is available
                        // TODO: Why not use the closure parameter `webView`? Would avoid to test its real value.
                        webViewRef.value?.let {
                            run {
                                webViewViewModel.configurePasskeys(it)
                            }
                        }
                    }
                )
            }

            //bottom sheet for import files
            if (showBottomSheet) {
                ImportFileBottomSheet(
                    sheetState,
                    onDismissRequest = {
                        showBottomSheet = false
                        activity.cancelFileChooser()
                    },
                    onFileSelected = {
                        showBottomSheet = false
                        val intent = activity.fileChooserParams?.createIntent()
                            ?: Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                type = "*/*"
                                addCategory(Intent.CATEGORY_OPENABLE)
                                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                            }

                        activity.filePickerLauncher.launch(intent)
                    },
                    onCameraSelected = {
                        PermissionManager(activity).requestCameraPermission { granted ->
                            if (granted) {
                                showBottomSheet = false
                                activity.cameraImageUri = FileUtils(activity).createCameraImageUri()
                                activity.cameraLauncher.launch(activity.cameraImageUri!!)
                            } else {
                                //if permission is denied, the bottomsheet remains visible and an alertDialog is displayed
                                activity.cancelFileChooser()
                                if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                        activity,
                                        Manifest.permission.CAMERA
                                    )
                                ) {
                                    showPermissionAlert = true
                                }
                            }
                        }
                    })
            }

            if (showPermissionAlert) {
                AlertDialog(
                    onDismissRequest = { showPermissionAlert = false },
                    confirmButton = {
                        PrimaryButton(
                            text = stringResource(R.string.allow_camera),
                            onClick = {
                                showPermissionAlert = false
                                val intent = Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", context.packageName, null)
                                )
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                            })
                    },
                    dismissButton = {
                        SecondaryButton(
                            text = stringResource(R.string.common_cancel),
                            onClick = { showPermissionAlert = false })
                    },
                    text = {
                        Text("Vous devez autoriser la caméra pour prendre une photo")
                    })
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
            goAuth = {},
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
            goAuth = {},
            goOnboarding = {})
    }
}
