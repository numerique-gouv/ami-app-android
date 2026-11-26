package fr.gouv.ami.home

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.WebView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import fr.gouv.ami.api.baseUrl
import fr.gouv.ami.data.models.Review
import fr.gouv.ami.data.models.ServiceUrls
import fr.gouv.ami.data.models.UrlAliases
import fr.gouv.ami.data.repository.getServiceUrlsWithoutFollowupFlow
import fr.gouv.ami.global.BaseViewModel
import fr.gouv.ami.webview.WebViewType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.net.URL

class WebViewViewModel(webviewType: WebViewType) : BaseViewModel() {
    private val TAG = this::class.java.simpleName
    var currentUrl by mutableStateOf(baseUrl)
    var canGoBack by mutableStateOf(false)
    val webViewType: WebViewType = webviewType

    var isRefreshing by mutableStateOf(false)
        private set

    var webView by mutableStateOf<WebView?>(null)

    var aliases by mutableStateOf(emptyArray<UrlAliases>())
    var authenticationUrls by mutableStateOf(emptyList<String>())

    var showSSLErrorBanner by mutableStateOf(false)
        private set

    private val _pageFinished = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val pageFinished = _pageFinished.asSharedFlow()

    private val _serviceUrlsWithoutFollowup = MutableStateFlow<List<URL>?>(null)
    val serviceUrlsWithoutFollowup = _serviceUrlsWithoutFollowup.asStateFlow()

    fun refreshServiceUrlsWithoutFollowup() {
        if (webViewType != WebViewType.SERVICE) return

        viewModelScope.launch {
            val flow = getServiceUrlsWithoutFollowupFlow()
            flow
                .catch { e ->
                    Log.e(TAG, "Error fetching service urls without followup", e)
                }
                .collect { response ->
                    if (response.isSuccessful) {
                        _serviceUrlsWithoutFollowup.value = response.body()?.serviceUrlsWithoutFollowup
                        Log.d(TAG, "The list of service urls without followup is ${_serviceUrlsWithoutFollowup.value}")
                    } else {
                        Log.e(
                            TAG,
                            "Error loading service urls without followup: ${response.code()} - ${response.message()}"
                        )
                    }
                }
        }
    }

    fun onUrlChanged(url: String) {
        Log.d(TAG, "onUrlChanged to url ${url}")
        currentUrl = url
    }

    fun showSSLErrorBanner() {
        showSSLErrorBanner = true
    }

    fun dismissSSLErrorBanner() {
        showSSLErrorBanner = false
    }

    fun notifyPageFinished() {
        _pageFinished.tryEmit(Unit)
        isRefreshing = false
    }

    override fun requestRefresh() {
        isRefreshing = true
        super.requestRefresh()
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun configurePasskeys(webView: WebView): Boolean {
        if (!WebViewFeature.isFeatureSupported(WebViewFeature.WEB_AUTHENTICATION)) {
            Log.e(TAG, "WebView does not support passkeys.")
            return false
        } else {
            webView.settings.javaScriptEnabled = true

            WebSettingsCompat.setWebAuthenticationSupport(
                webView.settings,
                WebSettingsCompat.WEB_AUTHENTICATION_SUPPORT_FOR_APP,
            )

            // Check if getWebauthenticationSupport may have been disabled by the WebView
            Log.d(TAG, "mode = ${WebSettingsCompat.getWebAuthenticationSupport(webView.settings)}")

            return true
        }
    }
}

class WebViewViewModelFactory(
    private val webViewType: WebViewType
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WebViewViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WebViewViewModel(webViewType) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
