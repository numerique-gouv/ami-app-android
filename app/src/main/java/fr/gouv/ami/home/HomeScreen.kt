package fr.gouv.ami.home

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.gouv.ami.navigation.NavigationViewModel
import fr.gouv.ami.ui.theme.AMITheme

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HomeScreen(webViewViewModel: WebViewViewModel, navigationViewModel: NavigationViewModel, onGoBack: () -> Unit = {}) {

    // Handle notification permission request when user reaches the welcome page
    NotificationPermissionHandler(webViewViewModel)

    /** UI **/

    WebViewScreen(webViewViewModel, navigationViewModel, onGoBack)

}

@Preview
@Composable
fun PreviewHomeScreenLight() {
    AMITheme {
        HomeScreen(viewModel(), NavigationViewModel())
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewHomeScreenDark() {
    AMITheme {
        HomeScreen(viewModel(), NavigationViewModel())
    }
}
