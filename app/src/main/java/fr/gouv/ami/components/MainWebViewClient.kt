package fr.gouv.ami.components

import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import fr.gouv.ami.api.baseUrl

class MainWebViewClient(private val baseUrl: String,
private val onBackBarChanged: (Boolean) -> Unit,
private val onUrlChanged: (String) -> Unit): WebViewClient() {
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
        super.doUpdateVisitedHistory(view, url, isReload)
    }
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)

        // Flush cookies to persistent storage immediately: this is to make sure the `token` cookie
        // received from the backend is stored for the next app restart.
        val cookieManager = CookieManager.getInstance()
        cookieManager.flush()
    }
}
