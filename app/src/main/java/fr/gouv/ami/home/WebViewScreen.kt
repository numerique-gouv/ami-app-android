package fr.gouv.ami.home

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.gouv.ami.Screen
import fr.gouv.ami.notifications.NotificationViewModel
import fr.gouv.ami.ui.theme.AMITheme
import fr.gouv.ami.webview.HomeWebView
import fr.gouv.ami.webview.ServiceWebView
import fr.gouv.ami.webview.WebViewType

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(
    startUrl: String,
    webViewType: WebViewType,
    notificationViewModel: NotificationViewModel,
    navigate: (Screen, String?, WebViewType?) -> Unit,
    backButton: () -> Unit
) {

    /** UI **/
    Log.d("homescreen", "this is a ${webViewType.name} webapp and it's startUrl is $startUrl")

    when (webViewType) {
        WebViewType.HOME -> HomeWebView(
            startUrl,
            notificationViewModel,
            navigate
        )

        WebViewType.SERVICE, WebViewType.CONNECTION -> ServiceWebView(
            startUrl,
            notificationViewModel,
            navigate,
            backButton,
            webViewType
        )
    }

}

@Preview
@Composable
fun PreviewWebViewScreenLight() {
    AMITheme {
        WebViewScreen(
            startUrl = "https://numerique.gouv.fr",
            webViewType = WebViewType.HOME,
            notificationViewModel = viewModel(),
            navigate = { _, _, _ -> },
            backButton = {})
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewWebViewScreenDark() {
    AMITheme {
        WebViewScreen(
            startUrl = "https://numerique.gouv.fr",
            webViewType = WebViewType.HOME,
            notificationViewModel = viewModel(),
            navigate = { _, _, _ -> },
            backButton = {})
    }
}
