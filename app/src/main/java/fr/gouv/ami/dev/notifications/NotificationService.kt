package fr.gouv.ami.dev.notifications

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import fr.gouv.ami.dev.MainActivity
import fr.gouv.ami.dev.R
import fr.gouv.ami.dev.data.AppDataBase
import fr.gouv.ami.dev.data.entity.NotificationEntity
import fr.gouv.ami.dev.data.repository.getNotifications
import fr.gouv.ami.dev.utils.Storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class NotificationService : Service() {

    val TAG = NotificationService::class.java.simpleName
    val CHANNEL_ID = "new notification"
    val CHANNEL_NAME = "new notification channel"

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
                            val notifications = value.body()?.map { it.toEntity() }
                            Log.d(TAG, "${notifications?.size} notifications")

                            //insert all notifications
                            notifications?.let {
                                AppDataBase.getDatabase(baseContext).notificationDao()
                                    .insertAll(notifications)
                            }

                            //get all notification not notified
                            val newNotifications =
                                AppDataBase.getDatabase(baseContext).notificationDao()
                                    .getNotNotified()
                            Log.d(TAG, "${newNotifications.size} new notifications")

                            if (newNotifications.size == 1) { //display 1 notification
                                createOnNotification(newNotifications[0])
                            } else if (newNotifications.size > 1) { //display many notifications
                                createManyNotifications(newNotifications)
                            }

                            newNotifications.forEach { notification ->
                                AppDataBase.getDatabase(baseContext).notificationDao()
                                    .setIsNotified(notification.id, true)

                            }
                        }
                }
                delay(1000 * 3 * 60) // check notifications every 3 minutes
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun createOnNotification(notification: NotificationEntity) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(notification.title)
            .setContentText(notification.message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(notification.id, builder.build())
        }
    }

    @SuppressLint("MissingPermission")
    private fun createManyNotifications(notifications: List<NotificationEntity>) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )


        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("${notifications.size} nouveaux messages")
            .setContentText("Appuyez pour les lire")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(notifications[0].id, builder.build())
        }
    }
}