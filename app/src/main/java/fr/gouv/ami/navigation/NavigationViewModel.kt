package fr.gouv.ami.navigation

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import fr.gouv.ami.api.baseUrl

/** Native screens that can overlay the WebView */
enum class NativeScreen {
    ReviewApp,
    Settings,
}

/** Represents a navigation entry in the unified back stack */
sealed class NavEntry {
    data class WebViewUrl(val url: String) : NavEntry()
    data class Screen(val screen: NativeScreen) : NavEntry()
}

/**
 * ViewModel that manages unified navigation history across WebView and native screens.
 * This replaces the separate WebView history and NavController back stack with a single source of truth.
 */
class NavigationViewModel : ViewModel() {
    val TAG: String = "NavigationViewModel"

    /** Unified navigation back stack */
    private val _backStack = mutableStateListOf<NavEntry>()
    val backStack: List<NavEntry> get() = _backStack

    /**
     * Initialize the back stack with a starting entry.
     * Must be called once when the ViewModel is first created.
     */
    fun initialize(startEntry: NavEntry) {
        if (_backStack.isEmpty()) {
            _backStack.add(startEntry)
            Log.d(TAG, "initialize: backStack=${backStack}")
        }
    }

    /** Current navigation entry */
    val currentEntry: NavEntry? get() = _backStack.lastOrNull()

    /** Whether we can go back in the unified history */
    val canGoBack: Boolean get() = _backStack.size > 1

    /**
     * Push a WebView URL onto the back stack.
     * Called when the WebView navigates to a new URL.
     */
    fun pushUrl(url: String) {
        val current = currentEntry
        // Ignore WebView URL changes while on the ReviewApp screen
        if (current is NavEntry.Screen && current.screen == NativeScreen.ReviewApp) return
        // Avoid duplicates if navigating to the same URL
        if (current is NavEntry.WebViewUrl && current.url == url) return

        _backStack.add(NavEntry.WebViewUrl(url))
        Log.d(TAG, "pushUrl: ${url}, backStack=${backStack}")
    }

    /**
     * Push a native screen onto the back stack.
     */
    fun pushScreen(screen: NativeScreen) {
        // Avoid duplicates
        val current = currentEntry
        if (current is NavEntry.Screen && current.screen == screen) return

        _backStack.add(NavEntry.Screen(screen))
        Log.d(TAG, "pushScreen: ${screen}, backStack=${backStack}")
    }

    /**
     * Reset the back stack to the initial state (e.g., after selecting a review app).
     */
    fun reset(initialUrl: String = baseUrl) {
        _backStack.clear()
        _backStack.add(NavEntry.WebViewUrl(initialUrl))
        Log.d(TAG, "reset: backStack=${backStack}")
    }

    /**
     * Go back in the unified history.
     * @return The entry to navigate to, or null if at the root.
     */
    fun goBack(url: String?): NavEntry? {
        if (!canGoBack) return null

        _backStack.removeLast()
        while (url != null && canGoBack) {
            Log.d(TAG, "going back to url ${url}")
            val current = currentEntry
            if (current is NavEntry.Screen ||
                (current is NavEntry.WebViewUrl && current.url != url)) {
                Log.d(TAG, "removing current entry ${current}")
                _backStack.removeLast()
            } else {
                break
            }
        }
        Log.d(TAG, "goBack: backStack=${backStack}")
        return currentEntry
    }

    /**
     * Deal with url changes, and either add a navEntry, or a navScreen.
     */
    fun onUrlChanged(url: String) {
        when {
            url.contains("settings") -> { goSettings() }
            else -> { pushUrl(url) }
        }
    }

    /**
     * Go to the native settings page.
     */
    fun goSettings() {
        pushScreen(NativeScreen.Settings)
    }

}
