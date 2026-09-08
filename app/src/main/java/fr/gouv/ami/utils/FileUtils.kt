package fr.gouv.ami.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import fr.gouv.ami.BuildConfig
import fr.gouv.ami.R
import java.io.File

class FileUtils(val context: Context) {
    fun downloadFile(uri: Uri, contentDisposition: String, mimeType: String) {
        val request = DownloadManager.Request(uri)

        // Extract filename from contentDisposition or Uri
        val filename = getFileName(contentDisposition, uri)

        request.apply {
            setTitle(filename)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
            setMimeType(mimeType)
        }

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

        Toast.makeText(context, context.getString(R.string.download_started), Toast.LENGTH_SHORT)
            .show()
    }


    private fun getFileName(contentDisposition: String?, uri: Uri): String {
        return if (contentDisposition != null && contentDisposition.contains("filename=")) {
            contentDisposition.substringAfter("filename=").substringBefore(";").replace("\"", "")
        } else {
            uri.lastPathSegment ?: "fichier"
        }
    }

    fun createCameraImageUri(): Uri {
        val cameraDir = File(context.cacheDir, "camera")
        if (!cameraDir.exists()) {
            cameraDir.mkdirs()
        }

        val file = File.createTempFile(
            "photo_",
            ".jpg",
            cameraDir
        )

        return FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.fileprovider",
            file
        )

    }
}