package fr.gouv.ami.notifications

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import fr.gouv.ami.utils.ManagerLocalStorage

class TokenManager() {

    private val TAG = this::class.java.simpleName

    fun saveFirebaseToken(context: Context) {
        Log.d(TAG, "saveFirebaseToken")
        Log.d(
            TAG,
            "ManagerLocalStorage.getToken() = ${ManagerLocalStorage(context).getToken()}"
        )
        if (ManagerLocalStorage(context).getToken() == null) {
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        return@OnCompleteListener
                    }
                    if (task.result != null) {
                        Log.d(TAG, "save ${task.result!!}")
                        ManagerLocalStorage(context).saveToken(task.result!!)
                    }
                })
        }
    }
}