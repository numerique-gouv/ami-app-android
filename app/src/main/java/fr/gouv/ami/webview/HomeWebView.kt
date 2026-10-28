package fr.gouv.ami.webview

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.gouv.ami.Screen
import fr.gouv.ami.components.webview.WebViewScreen
import fr.gouv.ami.global.BaseScreen
import fr.gouv.ami.home.WebViewViewModel
import fr.gouv.ami.home.WebViewViewModelFactory

@Composable
fun HomeWebView(
    startUrl: String,
    navigate: (Screen, WebViewType, String?) -> Unit,
) {
    val webViewViewModel = viewModel<WebViewViewModel>(
        factory = WebViewViewModelFactory(WebViewType.HOME)
    )
    webViewViewModel.currentUrl = startUrl
    BaseScreen(viewModel = webViewViewModel) {
        WebViewScreen(
            webViewViewModel,
            navigate = navigate
        )
    }
}