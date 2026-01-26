package fr.gouv.ami.components

import android.content.Context
import androidx.lifecycle.viewModelScope
import fr.gouv.ami.global.BaseViewModel
import fr.gouv.ami.utils.LogsExporter
import kotlinx.coroutines.launch

class DownloadLogsViewModel : BaseViewModel() {

    fun shareLogs(context: Context, userFcHash: String? = null) {
        viewModelScope.launch {
            LogsExporter.shareLogcat(context, userFcHash)
        }
    }
}
