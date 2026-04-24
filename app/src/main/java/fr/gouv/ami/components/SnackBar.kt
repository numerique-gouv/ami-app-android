package fr.gouv.ami.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.gouv.ami.R
import fr.gouv.ami.ui.theme.AMITheme
import fr.gouv.ami.ui.theme.*
import kotlinx.coroutines.delay

enum class Behaviour {
    Automatic,
    Cancel,
    Close
}

@Composable
fun SnackBar(
    status: StatusType,
    title: String,
    behaviour: Behaviour,
    onDismiss: () -> Unit = {},
    action: () -> Unit = {}
) {
    var backgroundColor: Color
    var idIcon: Int

    when (status) {
        StatusType.Information -> {
            backgroundColor = BlueInformationBg
            idIcon = R.drawable.ic_information_information

        }

        StatusType.Error -> {
            backgroundColor = RedInformationBg
            idIcon = R.drawable.ic_information_error
        }

        StatusType.Success -> {
            backgroundColor = GreenInformationBg
            idIcon = R.drawable.ic_information_validation
        }

        StatusType.Warning -> {
            backgroundColor = OrangeInformationBg
            idIcon = R.drawable.ic_information_warning
        }
    }

    LaunchedEffect(Unit) {
        if (behaviour == Behaviour.Automatic) {
            delay(3000) //3 seconds
        } else {
            delay(5000) //5 seconds
        }
        onDismiss()
    }

    Card(
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painterResource(idIcon),
                contentDescription = "",
            )
            Text(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .weight(1f),
                text = title
            )
            when (behaviour) {
                Behaviour.Close -> {
                    IconButton(onClick = onDismiss) {
                        Image(
                            painterResource(R.drawable.ic_close),
                            contentDescription = "close",
                            colorFilter = ColorFilter.tint(BlueFranceSun113)
                        )
                    }
                }

                Behaviour.Cancel -> {
                    TextButton(onClick = {
                        action()
                        onDismiss()
                    }) {
                        Text(
                            text = stringResource(R.string.cancel),
                            color = BlueFranceSun113
                        )
                    }
                }

                Behaviour.Automatic -> {}
            }
        }
    }
}

@Preview
@Composable
fun PreviewSnackBarSuccess() {
    AMITheme {
        SnackBar(
            status = StatusType.Success,
            title = "Les notifications ont été activées",
            behaviour = Behaviour.Automatic
        )
    }
}

@Preview
@Composable
fun PreviewSnackBarWarning() {
    AMITheme {
        SnackBar(
            status = StatusType.Warning,
            title = "Les notifications ont été activées",
            behaviour = Behaviour.Close
        )
    }
}

@Preview
@Composable
fun PreviewSnackBarError() {
    AMITheme {
        SnackBar(
            status = StatusType.Error,
            title = "Les notifications ont été activées",
            behaviour = Behaviour.Cancel
        )
    }
}

@Preview
@Composable
fun PreviewSnackBarInformation() {
    AMITheme {
        SnackBar(
            status = StatusType.Information,
            title = "Les notifications ont été activées",
            behaviour = Behaviour.Automatic
        )
    }
}