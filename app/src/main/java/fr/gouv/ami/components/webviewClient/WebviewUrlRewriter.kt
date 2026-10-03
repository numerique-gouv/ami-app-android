package fr.gouv.ami.components.webviewClient

import android.net.Uri

object WebviewUrlRewriter {

    private const val SP_DOMAIN = "service-public.gouv.fr"

    fun rewrite(uri: Uri): Uri? {
        if (uri.host?.endsWith(SP_DOMAIN) == false) {
            return null
        }

        return uri.buildUpon().appendQueryParameter("view", "mobile").build()
    }
}