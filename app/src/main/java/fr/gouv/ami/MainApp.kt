package fr.gouv.ami

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.gouv.ami.api.baseUrl
import fr.gouv.ami.dev.home.ReviewAppsScreen
import fr.gouv.ami.home.HomeScreen
import fr.gouv.ami.home.WebViewViewModel
import fr.gouv.ami.navigation.NavEntry
import fr.gouv.ami.navigation.NavigationViewModel
import fr.gouv.ami.navigation.NativeScreen
import fr.gouv.ami.settings.SettingsScreen

@Composable
fun HomeApp() {
    val webViewViewModel = viewModel<WebViewViewModel>()
    val navigationViewModel = viewModel<NavigationViewModel>()

    val startEntry = if (BuildConfig.FLAVOR == "staging") {
        NavEntry.Screen(NativeScreen.ReviewApp)
    } else {
        NavEntry.WebViewUrl(baseUrl)
    }
    navigationViewModel.initialize(startEntry)

    val currentEntry = navigationViewModel.currentEntry

    BackHandler(enabled = navigationViewModel.canGoBack) {
        val previousEntry = currentEntry
        navigationViewModel.goBack()
        // When going back from a WebView URL, sync the WebView's navigation history
        if (previousEntry is NavEntry.WebViewUrl) {
            webViewViewModel.goBackInWebView?.invoke()
        }
    }

    Box {
        // WebView is always alive underneath native screen overlays
        // This allows us to keep the WebView's scroll position, avoid useless reloads,
        // keep the websocket opened...
        HomeScreen(
            webViewViewModel = webViewViewModel,
            navigationViewModel = navigationViewModel,
        )

        // Native screens overlay on top when active
        if (currentEntry is NavEntry.Screen) {
            when (currentEntry.screen) {
                NativeScreen.ReviewApp -> ReviewAppsScreen(
                    onSelectedReviewApp = {
                        navigationViewModel.reset(baseUrl)
                    }
                )
                NativeScreen.Settings -> SettingsScreen(
                    onBackButton = {
                        navigationViewModel.goBack()
                    },
                    webViewViewModel = webViewViewModel
                )
            }
        }
    }
}
