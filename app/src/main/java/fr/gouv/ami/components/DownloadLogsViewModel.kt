package fr.gouv.ami.components

import android.content.Context
import fr.gouv.ami.global.BaseViewModel
import fr.gouv.ami.utils.LogsExporter

class DownloadLogsViewModel : BaseViewModel() {

    fun shareLogs(context: Context, userFcHash: String? = null) {
        LogsExporter.shareLogcat(context, userFcHash)
    }
}
