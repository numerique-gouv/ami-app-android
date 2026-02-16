package fr.gouv.ami.components

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import fr.gouv.ami.global.BaseViewModel
import fr.gouv.ami.utils.LogsExporter
import kotlinx.coroutines.launch

class DownloadLogsViewModel : BaseViewModel() {
    private val TAG = "DownloadLogsViewModel"

    fun shareLogs(context: Context, userFcHash: String? = null) {
        viewModelScope.launch {
            try {
                LogsExporter(userFcHash).shareLogcat(context)
            } catch (e: LogsExporter.UnableToAccessLogs) {
                Log.e(TAG, "UnableToAccessLogs")
                displayAlert(context, "Erreur", "Impossible d'accéder aux logs")
            } catch (e: LogsExporter.UnableToWriteLogFile) {
                Log.e(TAG, "UnableToAccessLogs")
                displayAlert(context, "Erreur", "Impossible d'enregistrer le fichier de logs")
            }
        }
    }

    private fun displayAlert(context: Context, title: String, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)

        builder.setNeutralButton("Ok") { dialog, which ->
        }

        builder.show()
    }
}
