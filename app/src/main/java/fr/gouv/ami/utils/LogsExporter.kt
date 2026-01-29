package fr.gouv.ami.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LogsExporter(
    val userId: String?
) {
    private val TAG = "LogsExporter"
    private val MAX_LOG_ENTRIES = 5000
    private val MAX_LOG_SIZE = 10 * 1024 * 1024 // 10 MB

    class UnableToAccessLogs() : Exception()
    class UnableToWriteLogFile() : Exception()

    /**
     * Collects device logs and opens a share dialog so the user can send them
     * via email, save to files, etc.
     *
     * Can throw exceptions: UnableToAccessLogs | UnableToWriteLogFile
     */
    suspend fun shareLogcat(context: Context) {
        // Run blocking I/O operations on background thread
        val temporaryLogEntriesFileUri: Uri = withContext(Dispatchers.IO) {
            val logEntries = async { grabLogEntries() }
            val logFileUri = async { writeToTemporaryFile(context, logEntries.await()) }
            logFileUri.await()
        }

        presentShareIntent(context, temporaryLogEntriesFileUri)
    }

    private suspend fun grabLogEntries(): String {
        // Execute logcat command to capture logs
        // -d: dump and exit (non-blocking)
        // -t 5000: last 5000 lines (reasonable limit)
        val stringBuilder = StringBuilder()

        try {
            val process = ProcessBuilder(listOf("logcat", "-t", "$MAX_LOG_ENTRIES"))
                .redirectErrorStream(true)
                .start()

            var currentSize = 0
            process.inputStream.bufferedReader().useLines { lines ->
                for (line in lines) {
                    currentSize += line.toByteArray(Charsets.UTF_8).size
                    if (currentSize > MAX_LOG_SIZE) {
                        break
                    }
                    stringBuilder.append(line).append("\n")
                }
            }
            return String(stringBuilder.toString().toByteArray(Charsets.UTF_8))

        } catch (e: Exception) {
            throw UnableToAccessLogs()
        }
    }

    private suspend fun writeToTemporaryFile(context: Context, content: String): Uri {
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(Date())

        // Generate filename with optional user hash
        val filename = if (!userId.isNullOrBlank()) {
            "ami_logs_android_${userId}_$timestamp.txt"
        } else {
            "ami_logs_android_$timestamp.txt"
        }

        try {
            // Create the logs directory in cache if it doesn't exist
            val logsDir = File(context.cacheDir, "logs")
            if (!logsDir.exists()) {
                logsDir.mkdirs()
            }

            // Write logs to file
            val file = File(logsDir, filename)
            file.writeText(content, Charsets.UTF_8)

            Log.d(TAG, "Logs saved to: ${file.absolutePath} (${file.length()} bytes)")

            // Create a content URI using FileProvider (required for sharing files on Android 7+)
            return FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            throw UnableToWriteLogFile()
        }
    }

    private fun presentShareIntent(context: Context, fileUri: Uri) {
        // Create share intent
        // Using "*/*" instead of "text/plain" because some apps (like Tchap)
        // interpret text/plain as inline text rather than a file attachment
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_STREAM, fileUri)
            putExtra(Intent.EXTRA_SUBJECT, "AMI App Logs")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Launch the share chooser
        val chooserIntent = Intent.createChooser(shareIntent, null)
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)
    }
}
