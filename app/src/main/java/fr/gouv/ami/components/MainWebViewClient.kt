package fr.gouv.ami.components

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.net.http.SslError
import android.os.Build
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

        // Try launching the URL in an external app, in case it's a deeplink.
        val url = request?.url?.toString() ?: return false
        val context = view?.context ?: return false

        if (Build.VERSION.SDK_INT >= 30) {
            return launchNativeApi30(context, url)
        } else {
            return launchNativeBeforeApi30(context, url)
        }
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

private fun launchNativeApi30(context: Context, url: String): Boolean {
    Log.d("MainWebViewClient", "API at of after 30")
    val nativeAppIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        addCategory(Intent.CATEGORY_BROWSABLE)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_ACTIVITY_REQUIRE_NON_BROWSER)
    }
    return try {
        context.startActivity(nativeAppIntent)
        Log.d("MainWebViewClient", "Found a native app, launching it")
        true
    } catch (e: ActivityNotFoundException) {
        Log.d("MainWebViewClient", "Didn't find a native app")
        false
    }
}

private fun launchNativeBeforeApi30(context: Context, url: String): Boolean {
    Log.d("MainWebViewClient", "API before 30")
    val pm = context.packageManager

    // Get all Apps that resolve a generic url
    val browserActivityIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://")).apply {
        addCategory(Intent.CATEGORY_BROWSABLE)
    }
    val genericResolvedList = extractPackageNames(pm.queryIntentActivities(browserActivityIntent, 0))
    Log.d("MainWebViewClient", "Native apps that can open any url: $genericResolvedList")

    // Get all apps that resolve the specific Url
    val specializedActivityIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        addCategory(Intent.CATEGORY_BROWSABLE)
    }
    val resolvedSpecializedList = extractPackageNames(pm.queryIntentActivities(specializedActivityIntent, 0)).toMutableSet()
    Log.d("MainWebViewClient", "Native apps that can open the following url $url: $resolvedSpecializedList")

    // Keep only the Urls that resolve the specific, but not the generic urls.
    resolvedSpecializedList.removeAll(genericResolvedList)
    Log.d("MainWebViewClient", "Specialized apps: $resolvedSpecializedList")

    // If the list is empty, no native app handlers were found.
    if (resolvedSpecializedList.isEmpty()) {
        Log.d("MainWebViewClient", "Didn't find a native app")
        return false
    }

    // We found native handlers. Launch the Intent.
    specializedActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(specializedActivityIntent)
    Log.d("MainWebViewClient", "Found a native app, launching it")
    return true
}

private fun extractPackageNames(resolveInfos: List<ResolveInfo>): Set<String> {
    return resolveInfos.map { it.activityInfo.packageName }.toSet()
}
