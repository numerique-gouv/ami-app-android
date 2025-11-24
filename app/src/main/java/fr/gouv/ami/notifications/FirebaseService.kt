package fr.gouv.ami.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import fr.gouv.ami.MainActivity
import fr.gouv.ami.R
import fr.gouv.ami.utils.ManagerLocalStorage

class FirebaseService : FirebaseMessagingService() {

    private val TAG = this::class.java.simpleName

    companion object {
        val IS_NOTIFIED = "isNotified"
    }

    val CHANNEL_NAME = "firebase channel"
    val CHANNEL_ID = "1000"

    override fun onNewToken(token: String) {
        Log.d(TAG, token)
        ManagerLocalStorage(this).saveToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        createNotificationChannel()

        Log.d(TAG, message.notification?.title ?: "message reçu")
        val messageBody = message.notification

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(IS_NOTIFIED, true)
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(messageBody?.title)
            .setContentText(messageBody?.body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            notificationBuilder.setSmallIcon(R.drawable.ic_notification)
        } else {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
        }

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@with
            }
            // notificationId is a unique int for each notification that you must define.
            notify(0, notificationBuilder.build())
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        // Register the channel with the system.
        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}