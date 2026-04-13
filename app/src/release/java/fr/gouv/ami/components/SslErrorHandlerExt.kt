package fr.gouv.ami.components

import android.webkit.SslErrorHandler

internal fun SslErrorHandler.handleSslError(): Boolean = false
