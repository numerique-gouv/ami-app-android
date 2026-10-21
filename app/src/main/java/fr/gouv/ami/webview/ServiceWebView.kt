package fr.gouv.ami.webview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.gouv.ami.Screen
import fr.gouv.ami.components.BackBar
import fr.gouv.ami.components.webview.WebViewScreen
import fr.gouv.ami.global.BaseScreen
import fr.gouv.ami.home.WebViewViewModel

@Composable
fun ServiceWebView(
    webViewViewModel: WebViewViewModel,
    navigate: (Screen) -> Unit,
    backButton: () -> Unit
) {

    BaseScreen(viewModel = webViewViewModel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            BackBar {
                backButton()
            }

            WebViewScreen(
                webViewViewModel,
                navigate = navigate
            )
        }
    }
}