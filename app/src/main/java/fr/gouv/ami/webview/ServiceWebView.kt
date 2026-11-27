package fr.gouv.ami.webview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.gouv.ami.Screen
import fr.gouv.ami.components.BackBar
import fr.gouv.ami.components.ServiceWithoutFollowup
import fr.gouv.ami.components.webview.AMIWebView
import fr.gouv.ami.global.BaseScreen
import fr.gouv.ami.home.WebViewViewModel
import fr.gouv.ami.home.WebViewViewModelFactory
import fr.gouv.ami.notifications.NotificationViewModel
import java.net.URL

@Composable
fun ServiceWebView(
    startUrl: String,
    notificationViewModel: NotificationViewModel,
    navigate: (Screen, String?, WebViewType?) -> Unit,
    backButton: () -> Unit,
    webViewType: WebViewType
) {
    val webViewViewModel = viewModel<WebViewViewModel>(
        factory = WebViewViewModelFactory(webViewType)
    )
    webViewViewModel.currentUrl = startUrl

    val serviceUrls by webViewViewModel.serviceUrlsWithoutFollowup.collectAsState()
    val displayIsNotFollowup = remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        webViewViewModel.refreshServiceUrlsWithoutFollowup()
    }

    BaseScreen(viewModel = webViewViewModel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            BackBar {
                backButton()
            }

            // startUrl may not start with the service URL after a redirect
            if (serviceUrls?.contains(URL(startUrl)) == true && displayIsNotFollowup.value) {
                ServiceWithoutFollowup {
                    displayIsNotFollowup.value = false
                }
            }

            AMIWebView(
                webViewViewModel,
                notificationViewModel,
                navigate = navigate
            )
        }
    }
}