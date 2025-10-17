package fr.gouv.ami.dev.home

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import fr.gouv.ami.dev.ui.theme.AMITheme

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HomeScreen() {

    val mUrl = "https://ami-back-staging.osc-fr1.scalingo.io"

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            factory = {
                WebView(it).apply {
                    settings.javaScriptEnabled = true
                    webViewClient = WebViewClient()
                    loadUrl(mUrl)
                }
            })
    }
}

@Preview
@Composable
fun PreviewHomeScreenLight() {
    AMITheme {
        HomeScreen()
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewHomeScreenDark() {
    AMITheme {
        HomeScreen()
    }
}