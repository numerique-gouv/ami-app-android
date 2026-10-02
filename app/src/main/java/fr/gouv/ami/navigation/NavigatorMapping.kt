package fr.gouv.ami.navigation

import fr.gouv.ami.Screen

object NavigatorMapping {

    private val routes = mapOf(
        PromotedUrls.WELCOME_NOTIFICATION_ACTIVATION
                to Screen.Onboarding,

        PromotedUrls.PREFERENCES_NOTIFICATIONS_ACTIVATION
                to Screen.Settings
    )

    fun resolve(alias: PromotedUrls): Screen? =
        routes[alias]
}