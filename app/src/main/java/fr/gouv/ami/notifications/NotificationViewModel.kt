package fr.gouv.ami.notifications

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fr.gouv.ami.global.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class NotificationViewModel: BaseViewModel() {
    private val _notificationPermissionRequested = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val notificationPermissionRequested = _notificationPermissionRequested.asSharedFlow()

    var showNotificationPermissionGrantedBanner by mutableStateOf(false)
        private set

    private val _openNotificationSettings = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val openNotificationSettings = _openNotificationSettings.asSharedFlow()

    private val _executeJavaScript = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val executeJavaScript = _executeJavaScript.asSharedFlow()

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
}