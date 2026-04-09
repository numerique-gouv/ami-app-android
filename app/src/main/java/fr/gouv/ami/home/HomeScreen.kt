package fr.gouv.ami.home

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.gouv.ami.Screen
import fr.gouv.ami.ui.theme.AMITheme

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HomeScreen(
    webViewViewModel: WebViewViewModel,
    onNavigate: (Screen) -> Unit,
    goAuth: () -> Unit,
    goOnboarding: () -> Unit,
    startUrl: String
) {

    /** UI **/

    WebViewScreen(webViewViewModel, onNavigate, goAuth = goAuth, goOnboarding = goOnboarding, startUrl = startUrl)
}

@Preview
@Composable
fun PreviewHomeScreenLight() {
    AMITheme {
        HomeScreen(viewModel(), onNavigate = {}, goAuth = {}, goOnboarding = {}, startUrl = "")
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewHomeScreenDark() {
    AMITheme {
        HomeScreen(viewModel(), onNavigate = {}, goAuth = {}, goOnboarding = {}, startUrl = "")
    }
}
