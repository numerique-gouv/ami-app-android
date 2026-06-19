package fr.gouv.ami.snackbar

import fr.gouv.ami.components.Behaviour
import fr.gouv.ami.components.StatusType
import fr.gouv.ami.data.models.SnackBarModel
import kotlinx.coroutines.flow.MutableStateFlow

object SnackBarManager {

    private val _snackbars = MutableStateFlow<List<SnackBarModel>>(emptyList())
    val snackbars = _snackbars

    fun show(
        status: StatusType,
        title: String,
        behaviour: Behaviour,
        onCancel: (() -> Unit)? = null
    ) {
        val new = SnackBarModel(
            status = status,
            title = title,
            behaviour = behaviour,
            onCancel = onCancel
        )

        val updated = (_snackbars.value + new).takeLast(3)
        _snackbars.value = updated
    }

    fun remove(id: String) {
        _snackbars.value = _snackbars.value.filterNot { it.id == id }
    }
}