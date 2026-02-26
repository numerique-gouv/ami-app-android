package fr.gouv.ami.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import fr.gouv.ami.R
import fr.gouv.ami.ui.theme.AMITheme

@Composable
fun DownloadLogsButton(
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = slideInVertically { it },
        exit = slideOutVertically { it }
    ) {
        PrimaryButton(text = stringResource(R.string.download_logs), onClick = onClick)
    }
}

@Preview
@Composable
fun PreviewDownloadLogsButton() {
    AMITheme {
        DownloadLogsButton(visible = true, onClick = {})
    }
}
