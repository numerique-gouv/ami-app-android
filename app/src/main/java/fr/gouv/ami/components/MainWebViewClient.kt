package fr.gouv.ami.components

import android.net.http.SslError
import android.util.Log
import android.webkit.CookieManager
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import fr.gouv.ami.BuildConfig

class MainWebViewClient(
    private val baseUrl: String,
    private val onBackBarChanged: (Boolean) -> Unit,
    private val onUrlChanged: (String) -> Unit,
    private val onLoadingChanged: (Boolean) -> Unit,
    private val onCanGoBackChanged: (Boolean) -> Unit = {},
    private val onPageFinished: () -> Unit = {},
): WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        // Show loader immediately on link click (before onPageStarted)
        onLoadingChanged(true)
        return false // Let WebView handle the navigation
    }

    override fun doUpdateVisitedHistory(
        view: WebView?,
        url: String?,
        isReload: Boolean
    ) {
        if (!url.isNullOrEmpty()) {
            onBackBarChanged(!url.contains(baseUrl))
            onUrlChanged(url)
            Log.d("HomeScreen", url)
        }
        view?.let { onCanGoBackChanged(it.canGoBack()) }
        super.doUpdateVisitedHistory(view, url, isReload)
    }

    override fun onPageStarted(
        view: WebView?,
        url: String?,
        favicon: android.graphics.Bitmap?
    ) {
        super.onPageStarted(view, url, favicon)
        onLoadingChanged(true)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        onLoadingChanged(false)

        // Flush cookies to persistent storage immediately: this is to make sure the `token` cookie
        // received from the backend is stored for the next app restart.
        val cookieManager = CookieManager.getInstance()
        cookieManager.flush()

        // Notify that page finished loading
        onPageFinished()
    }

    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        Log.w("WebView", "SSL Error on URL: ${error.url} - Error type: ${error.primaryError}")
        // For debug builds, proceed through SSL errors
        if (BuildConfig.DEBUG) {
            handler.proceed()
        } else {
            handler.cancel()
        }
    }
}
