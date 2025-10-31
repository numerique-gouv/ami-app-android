package fr.gouv.ami.dev.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import fr.gouv.ami.dev.R
import fr.gouv.ami.dev.data.repository.getNotifications
import fr.gouv.ami.dev.utils.Storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class NotificationService : Service() {

    val TAG = NotificationService::class.java.simpleName

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        createNotification()
        checkNotification()
    }

    // create a notification channel to keep the network active
    private fun createNotification() {
        val channelId = "notification_channel"

        val channel = NotificationChannel(
            channelId,
            "notification_channel",
            NotificationManager.IMPORTANCE_NONE
        )
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        (getSystemService((NOTIFICATION_SERVICE)) as NotificationManager).createNotificationChannel(
            channel
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("check the notifications in background")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setOngoing(true)
            .build()

        startForeground(2, notification)
    }

    private fun checkNotification() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {

                val userId = Storage.getInstance(baseContext).getItemInt("user_id")
                Log.d(TAG, "user_id is ${userId.toString()}")

                if (userId != null && userId != -1) {
                    getNotifications(userId)
                        .catch { e ->
                            Log.d(TAG, e.toString())
                        }
                        .collect { value ->
                            val notifications = value.body()
                            Log.d(TAG, "${notifications?.size} notifications")
                        }
                }
                delay(1000 * 3 * 60) // check notifications every 3 minutes
            }
        }
    }
}