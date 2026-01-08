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

        // Navigate back to home after user responds
        if (isGranted) {
            webViewViewModel.onGoHome(query=listOf("has_enabled_notifications"))
        } else {
            webViewViewModel.onGoHome()
        }
    }

    // Observe JavaScript event for notification permission request
    LaunchedEffect(Unit) {
        webViewViewModel.notificationPermissionRequested.collect {
            Log.d("NotificationPermission", "JavaScript event 'notification_permission_requested' received")
            handleNotificationPermissionRequest(
                context = context,
                webViewViewModel = webViewViewModel,
                permissionLauncher = { notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) }
            )
        }
    }
}

/**
 * Handles the logic for requesting notification permission.
 * Checks various conditions (Android version, permission status)
 * and either shows the dialog or navigates home.
 */
private fun handleNotificationPermissionRequest(
    context: Context,
    webViewViewModel: WebViewViewModel,
    permissionLauncher: () -> Unit
) {
    val androidVersion = Build.VERSION.SDK_INT
    val needsPermission = androidVersion >= Build.VERSION_CODES.TIRAMISU
    Log.d("NotificationPermission", "Android version: $androidVersion, needs permission: $needsPermission")

    if (needsPermission) {
        handleAndroid13PlusPermission(context, webViewViewModel, permissionLauncher)
    } else {
        // Android < 13: No permission dialog needed
        Log.d("NotificationPermission", "Android < 13, notifications are automatically allowed without dialog")
        webViewViewModel.onGoHome()
    }
}

/**
 * Handles permission request for Android 13+ (Tiramisu and above).
 */
private fun handleAndroid13PlusPermission(
    context: Context,
    webViewViewModel: WebViewViewModel,
    permissionLauncher: () -> Unit
) {
    val currentPermissionStatus = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
    )
    val isGranted = currentPermissionStatus == PackageManager.PERMISSION_GRANTED
    Log.d("NotificationPermission", "Current permission status: ${if (isGranted) "GRANTED" else "DENIED/NOT_ASKED"}")

    Log.d("NotificationPermission", "Launching permission dialog...")
    permissionLauncher()
}
