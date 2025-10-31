package fr.gouv.ami.dev.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootCompleted : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            context.startForegroundService(Intent(context, NotificationService::class.java))
        }
    }
}