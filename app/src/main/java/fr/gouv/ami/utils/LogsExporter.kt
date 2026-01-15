package fr.gouv.ami.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object LogsExporter {
    private const val TAG = "LogsExporter"

    /**
     * Collects device logs and opens a share dialog so the user can send them
     * via email, save to files, etc.
     *
     * @param userFcHash Optional user identifier from localStorage to include in filename
     */
    fun shareLogcat(context: Context, userFcHash: String? = null) {
        try {
            // Create the logs directory in cache if it doesn't exist
            val logsDir = File(context.cacheDir, "logs")
            if (!logsDir.exists()) {
                logsDir.mkdirs()
            }

            // Generate a timestamped filename with optional user hash
            val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(Date())
            val filename = if (!userFcHash.isNullOrBlank()) {
                "ami_logs_android_${userFcHash}_$timestamp.txt"
            } else {
                "ami_logs_android_$timestamp.txt"
            }
            val logFile = File(logsDir, filename)

            // Execute logcat command to capture logs
            // -d: dump and exit (non-blocking)
            // -t 5000: last 5000 lines (reasonable limit)
            val process = Runtime.getRuntime().exec("logcat -d -t 5000")
            val logs = process.inputStream.bufferedReader().readText()
            process.waitFor()

            // Write logs to file
            logFile.writeText(logs)

            Log.d(TAG, "Logs saved to: ${logFile.absolutePath} (${logFile.length()} bytes)")

            // Create a content URI using FileProvider (required for sharing files on Android 7+)
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                logFile
            )

            // Create share intent
            // Using "*/*" instead of "text/plain" because some apps (like Tchap)
            // interpret text/plain as inline text rather than a file attachment
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "*/*"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, "AMI App Logs - $timestamp")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // Launch the share chooser
            val chooserIntent = Intent.createChooser(shareIntent, null)
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooserIntent)

        } catch (e: Exception) {
            Log.e(TAG, "Failed to share logs", e)
        }
    }
}
