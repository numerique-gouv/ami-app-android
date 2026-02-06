package fr.gouv.ami.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fr.gouv.ami.api.baseUrl
import fr.gouv.ami.global.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class WebViewViewModel : BaseViewModel() {
    var currentUrl by mutableStateOf(baseUrl)
    var lastUrl by mutableStateOf(baseUrl) //not used for now

    /** Set by WebViewScreen to allow the top-level BackHandler to trigger webView.goBack() */
    var goBackInWebView: (() -> Unit)? = null

    var isOnContactPage by mutableStateOf(false)
        private set

    private val _notificationPermissionRequested = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val notificationPermissionRequested = _notificationPermissionRequested.asSharedFlow()

    var showNotificationPermissionGrantedBanner by mutableStateOf(false)
        private set

    private val _openNotificationSettings = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val openNotificationSettings = _openNotificationSettings.asSharedFlow()

    private val _executeJavaScript = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val executeJavaScript = _executeJavaScript.asSharedFlow()

    private val _pageFinished = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val pageFinished = _pageFinished.asSharedFlow()

    fun onUrlChanged(url: String) {
        if (currentUrl.contains(baseUrl)) {
            lastUrl = currentUrl
        }
        currentUrl = url
        isOnContactPage = url.contains("/#/contact")
    }

    fun onBackPressed() {
        onGoHome()
    }

    fun onGoHome() {
        currentUrl = baseUrl
    }

    fun triggerNotificationPermissionRequest() {
        _notificationPermissionRequested.tryEmit(Unit)
    }

    fun showNotificationPermissionGrantedBanner() {
        showNotificationPermissionGrantedBanner = true
    }

    fun dismissNotificationPermissionGrantedBanner() {
        showNotificationPermissionGrantedBanner = false
    }

    fun openNotificationSettings() {
        _openNotificationSettings.tryEmit(Unit)
    }

    fun runJavaScript(script: String) {
        _executeJavaScript.tryEmit(script)
    }

    fun setLocalStorage(key: String, value: String) {
        val script = "localStorage.setItem('$key', '$value');"
        runJavaScript(script)
    }

    fun notifyPageFinished() {
        _pageFinished.tryEmit(Unit)
    }
}
