package fr.gouv.ami

val nativeRoutes: Map<String, Screen> = mapOf(
    "/#/settings" to Screen.Settings,
    "/#/notifications-welcome-page" to Screen.Onboarding
)

fun findNativeScreen(url: String): Screen? =
    nativeRoutes.entries.firstOrNull { (path, _) -> url.contains(path) }?.value
