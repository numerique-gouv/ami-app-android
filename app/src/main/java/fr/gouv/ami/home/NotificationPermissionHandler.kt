package fr.gouv.ami.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
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

    // Initialize localStorage with current permission state when page finishes loading
    // Doing it only once when creating the Webview fails: it tries to execute javascript too early
    // and doesn't properly set the localStorage.
    LaunchedEffect(Unit) {
        webViewViewModel.pageFinished.collect {
            val isGranted = isPermissionGranted(context)
            webViewViewModel.setLocalStorage(
                "notifications_enabled",
                if (isGranted) "true" else "false"
            )
            Log.d(
                "NotificationPermission",
                "Initialized localStorage notifications_enabled: $isGranted"
            )
        }
    }

    // Permission launcher
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d("NotificationPermission", "User responded: ${if (isGranted) "GRANTED" else "DENIED"}")

        if (isGranted) {
            webViewViewModel.showNotificationPermissionGrantedBanner()
            webViewViewModel.setLocalStorage("notifications_enabled", "true")
        } else {
            webViewViewModel.setLocalStorage("notifications_enabled", "false")
        }
    }

    // Observe event for notification permission request
    LaunchedEffect(Unit) {
        webViewViewModel.notificationPermissionRequested.collect {
            Log.d(
                "NotificationPermission",
                "JavaScript event 'notification_permission_requested' received"
            )

            val isGranted = isPermissionGranted(context)
            val shouldShowRationale = shouldShowRequestPermissionRationale(context)
            val hasRequestedBefore = hasRequestedPermissionBefore(context)
            val isPermanentlyDenied = !isGranted && !shouldShowRationale && hasRequestedBefore

            Log.d(
                "NotificationPermission",
                "Permission status - granted: $isGranted, shouldShowRationale: $shouldShowRationale, hasRequestedBefore: $hasRequestedBefore, permanentlyDenied: $isPermanentlyDenied"
            )

            if (isPermanentlyDenied) {
                // Permission was permanently denied, open settings instead
                Log.d(
                    "NotificationPermission",
                    "Permission permanently denied, we can't show the permission popup anymore, last resort is displaying the OS notification settings"
                )
                openAppNotificationSettings(context)
            } else {
                // Mark that we've requested permission (before showing the dialog)
                markPermissionRequested(context)
                // Can still request permission
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Observe event to open notification settings
    LaunchedEffect(Unit) {
        webViewViewModel.openNotificationSettings.collect {
            Log.d(
                "NotificationPermission",
                "JavaScript event 'notification_permission_removed' received"
            )
            openAppNotificationSettings(context)
        }
    }
}

/**
 * Opens the system settings page for app notifications.
 * This allows the user to manually manage notification permissions.
 */
private fun openAppNotificationSettings(context: Context) {
    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Android 8.0 and above - open notification settings
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
    } else {
        // Below Android 8.0 - open app details settings
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
    }

    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
    Log.d("NotificationPermission", "Opened app notification settings")
}

fun isPermissionGranted(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED
}

/**
 * Checks if we should show a rationale for requesting the permission.
 * Returns true if the user previously denied the permission but hasn't permanently denied it yet.
 * Returns false if:
 * - Permission was never requested before (first time)
 * - Permission was permanently denied (user selected "Don't ask again" or denied twice on Android 11+)
 */
private fun shouldShowRequestPermissionRationale(context: Context): Boolean {
    val activity = context as? Activity
    return activity?.let {
        ActivityCompat.shouldShowRequestPermissionRationale(
            it,
            Manifest.permission.POST_NOTIFICATIONS
        )
    } ?: false
}

private const val PREFS_NAME = "notification_permission_prefs"
private const val KEY_PERMISSION_REQUESTED = "permission_requested"

/**
 * Checks if we have requested the notification permission at least once before.
 * This helps distinguish between "never asked" and "permanently denied" states.
 */
private fun hasRequestedPermissionBefore(context: Context): Boolean {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getBoolean(KEY_PERMISSION_REQUESTED, false)
}

/**
 * Marks that we have requested the notification permission.
 * Should be called before launching the permission request.
 */
private fun markPermissionRequested(context: Context) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().putBoolean(KEY_PERMISSION_REQUESTED, true).apply()
    Log.d("NotificationPermission", "Marked permission as requested")
}
