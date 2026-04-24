package fr.gouv.ami.snackbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.gouv.ami.components.Behaviour
import fr.gouv.ami.components.SnackBar
import fr.gouv.ami.data.models.SnackBarModel

@Composable
fun SnackBarStack(
    snackbars: List<SnackBarModel>,
    onDismiss: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        snackbars.forEachIndexed { index, item ->
            key(item.id) {
                val offsetY = (index * 4).dp

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = -offsetY)
                ) {
                    SnackBar(
                        status = item.status,
                        title = item.title,
                        behaviour = item.behaviour,
                        onDismiss = { onDismiss(item.id) },
                        action = {
                            item.onCancel?.invoke()
                        },
                    )
                }
            }
        }
    }
}