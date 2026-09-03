package fr.gouv.ami.global

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import fr.gouv.ami.MainActivity

class PermissionManager(
    private val activity: MainActivity
) {

    fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestCameraPermission(
        onResult: (granted: Boolean) -> Unit
    ) {
        if (hasCameraPermission()) {
            onResult(true)
            return
        }

        activity.onPermissionResult = onResult

        activity.permissionLauncher.launch(
            Manifest.permission.CAMERA
        )
    }
}