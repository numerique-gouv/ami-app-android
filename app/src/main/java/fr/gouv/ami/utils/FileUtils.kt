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
import java.net.URLDecoder
import java.nio.charset.Charset

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
        return contentDisposition?.let { content ->
            extractFileName(content)
        }
            ?: uri.lastPathSegment
            ?: "fichier"
    }

    private fun extractFileName(contentDisposition: String): String? {

        //format is filename*=
        val filenameStarRegex = Regex(
            """filename\*\s*=\s*([^']*)'[^']*'(?:"([^"]+)"|([^;]+))""",
            RegexOption.IGNORE_CASE
        )

        filenameStarRegex.find(contentDisposition)?.let { match ->
            if (match.groupValues.size >= 3) {
                val charsetName = match.groupValues[1]
                //groups[2] is filename*="..."
                //groups[3] is filename*=...
                val encodedFilename =
                    match.groups[2]?.value
                        ?: match.groups[3]?.value

                val charset = runCatching {
                    Charset.forName(charsetName)
                }.getOrElse {
                    Charsets.UTF_8
                }

                return URLDecoder
                    .decode(encodedFilename, charset.name())
                    .trim()
            }
        }

        // format is filename=
        val filenameRegex = Regex(
            """filename\s*=\s*(?:"([^"]+)"|([^;]+))""",
            RegexOption.IGNORE_CASE
        )

        filenameRegex.find(contentDisposition)?.let { match ->
            //groups[1] is filename="..."
            //groups[2] is filename=...
            return (match.groups[1]?.value ?: match.groups[2]?.value)
                ?.trim()
        }

        return null
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