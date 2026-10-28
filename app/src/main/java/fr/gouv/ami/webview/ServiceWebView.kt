package fr.gouv.ami.webview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.gouv.ami.Screen
import fr.gouv.ami.components.BackBar
import fr.gouv.ami.components.webview.WebViewScreen
import fr.gouv.ami.global.BaseScreen
import fr.gouv.ami.home.WebViewViewModel
import fr.gouv.ami.home.WebViewViewModelFactory

@Composable
fun ServiceWebView(
    startUrl: String,
    navigate: (Screen, WebViewType, String?) -> Unit,
    backButton: () -> Unit
) {
    val webViewViewModel = viewModel<WebViewViewModel>(
        factory = WebViewViewModelFactory(WebViewType.SERVICE)
    )
    webViewViewModel.currentUrl = startUrl
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