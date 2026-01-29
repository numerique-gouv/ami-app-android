package fr.gouv.ami.settings

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.gouv.ami.R
import fr.gouv.ami.components.PrimaryButton
import fr.gouv.ami.components.SecondaryButton
import fr.gouv.ami.components.Title
import fr.gouv.ami.global.BaseScreen
import fr.gouv.ami.home.NotificationPermissionHandler
import fr.gouv.ami.home.WebViewViewModel
import fr.gouv.ami.home.markPermissionRequested
import fr.gouv.ami.ui.theme.AMITheme

@Composable
fun OnboardingNotificationScreen(webViewViewModel: WebViewViewModel, onChooseClick: () -> Unit) {
    val context = LocalContext.current

    NotificationPermissionHandler(webViewViewModel)

    BaseScreen(viewModel = webViewViewModel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painterResource(R.drawable.img_notification),
                contentDescription = "image de notification"
            )
            Title(
                text = stringResource(R.string.onboarding_title),
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Text(
                stringResource(R.string.onboarding_description),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            PrimaryButton(stringResource(R.string.enable)) {
                Log.d("test", "prout")
                webViewViewModel.triggerNotificationPermissionRequest()
                onChooseClick()
            }
            SecondaryButton(stringResource(R.string.maybe_later)) {
                markPermissionRequested(context)
                onChooseClick()
            }
        }
    }
}

@Preview
@Composable
fun PreviewOnboardingNotificationScreenLight() {
    AMITheme {
        OnboardingNotificationScreen(viewModel()) {}
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewOnboardingNotificationScreenDark() {
    AMITheme {
        OnboardingNotificationScreen(viewModel()) {}
    }
}
