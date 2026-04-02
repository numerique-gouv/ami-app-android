package fr.gouv.ami.components

import android.util.Log
import android.webkit.SslErrorHandler

internal fun SslErrorHandler.handleSslError(): Boolean {
    Log.w("WebView", "Bypassing SSL error (debug build)")
    proceed()
    return true
}
