package fr.gouv.ami.data.models

import fr.gouv.ami.components.Behaviour
import fr.gouv.ami.components.StatusType
import java.util.UUID

data class SnackBarModel(
    val id: String = UUID.randomUUID().toString(),
    val status: StatusType,
    val title: String,
    val behaviour: Behaviour,
    val onCancel: (() -> Unit)? = null
)