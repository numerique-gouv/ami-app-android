package fr.gouv.ami.global

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import fr.gouv.ami.MainActivity

class PermissionManager(
    private val activity: MainActivity
) {

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocationPermission(
        onResult: (granted: Boolean) -> Unit
    ) {
        Log.d("PermissionManager", "request location permission")
        if (hasLocationPermission()) {
            onResult(true)
            return
        }

        activity.onPermissionLocationResult = onResult

        activity.permissionLocationLauncher.launch(
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    private fun hasCameraPermission(): Boolean {
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

        activity.onPermissionCameraResult = onResult

        activity.permissionCameraLauncher.launch(
            Manifest.permission.CAMERA
        )
    }
}