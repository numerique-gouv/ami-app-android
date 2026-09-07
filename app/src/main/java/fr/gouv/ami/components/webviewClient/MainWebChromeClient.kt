package fr.gouv.ami.components.webviewClient

import android.net.Uri
import android.util.Log
import android.webkit.JsResult
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import fr.gouv.ami.MainActivity

class MainWebChromeClient(
    val activity: MainActivity,
    val visibilityModalFilesChanged: (Boolean) -> Unit
) : WebChromeClient() {
    val TAG = this::class.java.simpleName

    // Required for Android WebView to handle beforeunload confirmation dialogs
    override fun onJsBeforeUnload(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        Log.d(TAG, "onJsBeforeUnload is called")
        return super.onJsBeforeUnload(view, url, message, result)
    }

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        Log.d(TAG, "onShowFileChooser is called")

        activity.filePathCallback = filePathCallback
        activity.fileChooserParams = fileChooserParams
        visibilityModalFilesChanged(true)

        return true
    }
}