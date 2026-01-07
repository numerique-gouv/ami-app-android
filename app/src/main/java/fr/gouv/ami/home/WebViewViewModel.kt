package fr.gouv.ami.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fr.gouv.ami.api.baseUrl
import fr.gouv.ami.global.BaseViewModel

class WebViewViewModel : BaseViewModel() {
    var currentUrl by mutableStateOf(baseUrl)
    var lastUrl by mutableStateOf(baseUrl) //not used for now

    fun onUrlChanged(url: String) {
        if (currentUrl.contains(baseUrl)) {
            lastUrl = currentUrl
        }
        currentUrl = url
    }

    fun onBackPressed() {
        currentUrl = baseUrl //back to home
    }
}