package fr.gouv.ami.dev.home

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import fr.gouv.ami.dev.utils.Storage

class AmiWebViewClient(val context: Context): WebViewClient() {

    private val handler = Handler(Looper.getMainLooper())

    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        return false
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        getItem(view, "user_id")
    }

    fun getItem(view: WebView?, item: String) {
        var itemValue: String?
        val runnable = object : Runnable {
            override fun run() {
                view?.evaluateJavascript(
                    "(function() { return localStorage.getItem('$item'); })();"
                ) { value ->
                    if (value?.removeSurrounding("\"") != null && value.removeSurrounding("\"") != "null") {
                        itemValue = value.removeSurrounding("\"")
                        Storage.getInstance(context = context).saveItemString(item, itemValue)
                    } else {
                        Storage.getInstance(context).clearItem(item)
                    }
                }

                handler.postDelayed(this, 3000)
            }
        }
        handler.postDelayed(runnable, 100)
    }
}