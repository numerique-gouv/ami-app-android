package fr.gouv.ami.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.webkit.CookieManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import fr.gouv.ami.BuildConfig
import fr.gouv.ami.MainActivity
import fr.gouv.ami.R
import fr.gouv.ami.api.apiService
import fr.gouv.ami.api.baseUrl
import fr.gouv.ami.data.models.Subscription
import fr.gouv.ami.data.models.SubscriptionRequest
import fr.gouv.ami.utils.DeviceIdUtils
import fr.gouv.ami.utils.storage.LowStorageManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirebaseService : FirebaseMessagingService() {

    private val TAG = this::class.java.simpleName

    companion object {
        val APP_URL = "app_url"
        val NOTIFICATION_TITLE = "title"
    }

    val CHANNEL_NAME = "firebase channel"
    val CHANNEL_ID = "1000"
    private val storageManager by lazy {
        LowStorageManager(applicationContext)
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "the new firebase token is $token")

        CoroutineScope(Dispatchers.IO).launch {
            storageManager.saveFirebaseToken(token)
            sendRegistration(token)
        }
    }

    suspend fun sendRegistration(token: String) {
        val cookieManager = CookieManager.getInstance()
        val cookies = cookieManager.getCookie(baseUrl)
        if (!cookies.isNullOrEmpty()) {
            val values = cookies.split(";")
            var bearer: String? = null
            for (cookie in values) {
                if (cookie.contains("token")) {
                    bearer = cookie.split("\"")[1]
                    Log.d(TAG, "bearer: $bearer")
                    storageManager.saveBearer(bearer)
                    break
                }
            }
            if (bearer != null) {
                val deviceId = DeviceIdUtils(applicationContext).getOrCreateDeviceId()
                val subscription = Subscription(
                    fcmToken = token,
                    deviceId = deviceId,
                    platform = "android",
                    appVersion = BuildConfig.VERSION_NAME,
                    model = getDeviceModel()
                )

                apiService.registrations(bearer, SubscriptionRequest(subscription))

            }
        }
    }


    override fun onMessageReceived(message: RemoteMessage) {
        createNotificationChannel()

        Log.d(TAG, message.notification?.title ?: "message reçu")
        val notification = message.notification

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            message.data.forEach { (key, value) -> putExtra(key, value) }
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(notification?.title)
            .setContentText(notification?.body)
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

    /**
     * Returns the device model for display/debugging purposes.
     * Example: "Samsung SM-G991B"
     */
    fun getDeviceModel(): String {
        return "${Build.MANUFACTURER} ${Build.MODEL}"
    }
}
