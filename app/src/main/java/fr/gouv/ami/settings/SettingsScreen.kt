package fr.gouv.ami.settings

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.gouv.ami.R
import fr.gouv.ami.global.BaseScreen
import fr.gouv.ami.home.NotificationPermissionHandler
import fr.gouv.ami.home.WebViewViewModel
import fr.gouv.ami.home.isPermissionGranted
import fr.gouv.ami.ui.theme.AMITheme

@Composable
fun SettingsScreen(webViewViewModel: WebViewViewModel, onBackButton: (url: String?) -> Unit) {

    NotificationPermissionHandler(webViewViewModel)
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var checked by remember { mutableStateOf(isPermissionGranted(context)) }

    // check notification permission when activity status is onResume
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                checked = isPermissionGranted(context)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    BaseScreen(
        hasTopBar = true,
        topBarTitle = stringResource(R.string.settings),
        onBackButton = onBackButton,
        viewModel = viewModel()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    stringResource(R.string.settings_notification),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                Switch(
                    checked = checked,
                    onCheckedChange = { checked ->
                        if (checked) {
                            webViewViewModel.triggerNotificationPermissionRequest()
                        } else {
                            webViewViewModel.openNotificationSettings()
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewSettingsScreenLight() {
    AMITheme {
        SettingsScreen(viewModel()) {}
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewSettingsScreenDark() {
    AMITheme {
        SettingsScreen(viewModel()) {}
    }
}
