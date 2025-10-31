package fr.gouv.ami.dev.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.edit
import fr.gouv.ami.dev.R

class Storage(context: Context) {

    companion object {
        @Volatile
        private var instance: Storage? = null
        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: Storage(context).also { instance = it }
        }
    }

    val sharedPreferences = context.getSharedPreferences(
        context.getString(R.string.app_name),
        Context.MODE_PRIVATE
    )

    fun saveItemString(item: String, value: String) {
        sharedPreferences.edit { putString(item, value) }
    }

    fun getItemString(item: String): String? {
        return sharedPreferences?.getString(item, null)
    }

    fun saveItemInt(item: String, value: Int) {
        sharedPreferences.edit { putInt(item, value) }
    }

    fun getItemInt(item: String): Int? {
        return sharedPreferences?.getInt(item, -1)
    }

    fun clearItem(item: String) {
        sharedPreferences.edit { remove(item)}
    }
}