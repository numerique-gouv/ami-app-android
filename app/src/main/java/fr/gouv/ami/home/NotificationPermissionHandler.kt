package fr.gouv.ami.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Handles notification permission request when triggered by JavaScript event.
 *
 * This composable observes the notification permission request event from the JavaScript bridge
 * (via NativeBridge.onEvent("notification_permission_requested")). When the event is received,
 * it triggers the Android permission dialog. After the user responds, it navigates back home.
 *
 * @param webViewViewModel The WebView view model to observe events and trigger navigation
 */
@Composable
fun NotificationPermissionHandler(webViewViewModel: WebViewViewModel) {
    val context = LocalContext.current

    // Permission launcher
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d("NotificationPermission", "User responded: ${if (isGranted) "GRANTED" else "DENIED"}")

        if (isGranted) {
            webViewViewModel.showNotificationPermissionGrantedBanner()
        }
        // Navigate back to home after user responds
        webViewViewModel.onGoHome()
    }

    // Observe JavaScript event for notification permission request
    LaunchedEffect(Unit) {
        webViewViewModel.notificationPermissionRequested.collect {
            Log.d("NotificationPermission", "JavaScript event 'notification_permission_requested' received")
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
