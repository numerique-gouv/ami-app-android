package fr.gouv.ami.home

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.gouv.ami.Screen
import fr.gouv.ami.ui.theme.AMITheme
import fr.gouv.ami.webview.HomeWebView
import fr.gouv.ami.webview.ServiceWebView
import fr.gouv.ami.webview.WebViewType

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HomeScreen(
    webViewViewModel: WebViewViewModel,
    navigate: (Screen) -> Unit,
    webViewType: WebViewType
) {

    /** UI **/

    when (webViewType) {
        WebViewType.HOME -> HomeWebView(
            webViewViewModel,
            navigate
        )

        WebViewType.SERVICE -> ServiceWebView(
            webViewViewModel,
            navigate)

        WebViewType.CONNEXION -> {}
    }

}

@Preview
@Composable
fun PreviewHomeScreenLight() {
    AMITheme {
        HomeScreen(webViewViewModel = viewModel(), navigate = {}, webViewType = WebViewType.HOME)
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewHomeScreenDark() {
    AMITheme {
        HomeScreen(webViewViewModel = viewModel(), navigate = {}, webViewType = WebViewType.HOME)
    }
}
