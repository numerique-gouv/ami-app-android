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

    fun saveFirebaseToken(context: Context) {
        Log.d(TAG, "saveFirebaseToken")
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                if (task.result != null) {
                    Log.d(TAG, "save ${task.result!!}")
                    CoroutineScope(Dispatchers.IO).launch {
                        LowStorageManager(context).saveFirebaseToken(task.result)
                    }
                }
            })

    }
}