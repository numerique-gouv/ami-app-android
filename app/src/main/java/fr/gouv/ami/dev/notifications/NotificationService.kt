package fr.gouv.ami.dev.notifications

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import fr.gouv.ami.dev.data.repository.getNotificationKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class NotificationService : Service() {

    val TAG = NotificationService::class.java.simpleName

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        CoroutineScope(Dispatchers.IO).launch {

            //register to notifications
            val notificationKey = getNotificationKey()
            notificationKey
                .catch { e -> Log.d(TAG, e.toString()) }
                .collect { response ->
                    val key = response.body()
                    Log.d(TAG, "The key is $key")
                }

        }

        return START_STICKY
    }
}