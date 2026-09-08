package fr.gouv.ami.notifications

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import fr.gouv.ami.utils.storage.LowStorageManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TokenManager() {

    private val TAG = this::class.java.simpleName

    fun saveFcmToken(context: Context) {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                if (task.result != null) {
                    Log.d(TAG, "save fcm token : ${task.result!!}")
                    CoroutineScope(Dispatchers.IO).launch {
                        LowStorageManager(context).saveFcmToken(task.result)
                    }
                }
            })

    }
}