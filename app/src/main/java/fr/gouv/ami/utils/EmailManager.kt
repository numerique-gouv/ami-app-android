package fr.gouv.ami.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import fr.gouv.ami.R
import androidx.core.net.toUri

class EmailManager(val context: Context) {

    val TAG = this::class.java.simpleName

    val managerStorage = ManagerLocalStorage(context)
    val deviceId = managerStorage.getOrCreateDeviceId()

    val target = "equipe-ami@numerique.gouv.fr"
    val body = context.getString(R.string.email_body, deviceId)

    fun emailTo(
        subject: String? = null,
    ) {
        val mailto = StringBuilder().apply {
            append("mailto:").append(target).append("?")
            subject?.let {
                append("subject=").append(Uri.encode(it)).append("&")
            }
            append("body=").append(Uri.encode(body)).append("&")

            replace(length - 1, length, "")
        }
        val intent = Intent().apply {
            action = Intent.ACTION_SENDTO
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
            addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            data = mailto.toString().toUri()
        }
        context.startActivity(intent)
    }
}