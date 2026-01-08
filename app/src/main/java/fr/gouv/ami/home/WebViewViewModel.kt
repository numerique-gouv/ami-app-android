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

    // SharedFlow for notification permission request event from JavaScript
    private val _notificationPermissionRequested = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val notificationPermissionRequested = _notificationPermissionRequested.asSharedFlow()

    // State for displaying notification permission granted banner
    var showNotificationPermissionGrantedBanner by mutableStateOf(false)
        private set

    fun onUrlChanged(url: String) {
        if (currentUrl.contains(baseUrl)) {
            lastUrl = currentUrl
        }
        currentUrl = url
    }

    fun onBackPressed() {
        onGoHome()
    }

    fun onGoHome() {
        currentUrl = baseUrl
    }

    /**
     * Triggers the notification permission request flow.
     * Called when the JavaScript bridge receives a "notification_permission_requested" event.
     */
    fun triggerNotificationPermissionRequest() {
        _notificationPermissionRequested.tryEmit(Unit)
    }

    /**
     * Shows the notification permission granted banner.
     */
    fun showNotificationPermissionGrantedBanner() {
        showNotificationPermissionGrantedBanner = true
    }

    /**
     * Dismisses the notification permission granted banner.
     */
    fun dismissNotificationPermissionGrantedBanner() {
        showNotificationPermissionGrantedBanner = false
    }
}
