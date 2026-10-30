package fr.gouv.ami.home

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import fr.gouv.ami.Screen
import fr.gouv.ami.api.baseUrl
import fr.gouv.ami.ui.theme.AMITheme
import fr.gouv.ami.webview.HomeWebView
import fr.gouv.ami.webview.ServiceWebView
import fr.gouv.ami.webview.WebViewType

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HomeScreen(
    startUrl: String,
    navigate: (Screen, String?) -> Unit,
    backButton: () -> Unit
) {

    /** UI **/
    val webViewType =
        if (startUrl.startsWith(baseUrl)) {
            WebViewType.HOME
        } else {
            WebViewType.SERVICE
        }

    when (webViewType) {
        WebViewType.HOME -> HomeWebView(
            startUrl,
            navigate
        )

        WebViewType.SERVICE -> ServiceWebView(
            startUrl,
            navigate,
            backButton
        )

        WebViewType.CONNECTION -> {}
    }

}

@Preview
@Composable
fun PreviewHomeScreenLight() {
    AMITheme {
        HomeScreen(
            startUrl = "https://numerique.gouv.fr",
            navigate = { _, _ -> },
            backButton = {})
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewHomeScreenDark() {
    AMITheme {
        HomeScreen(
            startUrl = "https://numerique.gouv.fr",
            navigate = { _, _ -> },
            backButton = {})
    }
}
