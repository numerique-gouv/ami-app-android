package fr.gouv.ami.webview

import androidx.compose.runtime.Composable
import fr.gouv.ami.Screen
import fr.gouv.ami.components.webview.WebViewScreen
import fr.gouv.ami.home.WebViewViewModel

@Composable
fun HomeWebView(
    webViewViewModel: WebViewViewModel,
    navigate: (Screen) -> Unit,
) {
    WebViewScreen(
        webViewViewModel,
        navigate = navigate
    )
}