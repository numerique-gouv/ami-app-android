package fr.gouv.ami

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import fr.gouv.ami.api.baseUrl
import fr.gouv.ami.dev.home.ReviewAppsScreen
import fr.gouv.ami.home.HomeScreen
import fr.gouv.ami.home.WebViewViewModel
import fr.gouv.ami.settings.SettingsScreen
import fr.gouv.ami.settings.FranceConnectScreen
import fr.gouv.ami.settings.OnboardingNotificationScreen

//list of all screens
enum class Screen {
    Home,
    ReviewApp,
    Settings,
    Onboarding,
    FranceConnect
}

@Composable
fun HomeApp(navController: NavHostController = rememberNavController()) {

    val TAG = object {}.javaClass.enclosingClass?.simpleName ?: "AMI"
    var webViewViewModel = viewModel<WebViewViewModel>()

    var startDestinationScreen = Screen.Home.name
    if (BuildConfig.FLAVOR == "staging") {
        startDestinationScreen = Screen.ReviewApp.name
    }

    NavHost(
        navController = navController,
        startDestination = startDestinationScreen
    ) {
        composable(route = Screen.Home.name) {
            HomeScreen(
                goSettings = {
                    navController.navigate(Screen.Settings.name)
                },
                goFranceConnect = {
                    navController.navigate(Screen.FranceConnect.name) {
                        launchSingleTop = true
                    }
                },
                goOnboarding = {
                    navController.navigate(Screen.Onboarding.name)
                },
                webViewViewModel = webViewViewModel
            )
        }
        composable(route = Screen.ReviewApp.name) {
            ReviewAppsScreen(
                onSelectedReviewApp = {
                    // We need to update the `currentUrl` here, as it was initialized with the
                    // baseUrl from the config, before we even selected the review app.
                    webViewViewModel.currentUrl = baseUrl
                    navController.navigate(Screen.Home.name)
                }
            )
        }
        composable(route = Screen.Settings.name) {
            SettingsScreen(
                onBackButton = {
                    navController.navigate(Screen.Home.name)
                },
                webViewViewModel = webViewViewModel
            )
        }
        composable(route = Screen.FranceConnect.name) {
            FranceConnectScreen(
                webViewViewModel = webViewViewModel,
                onFcClick = {
                    Log.d("MainApp", "user clicked on the France Connect login button")
                    navController.navigate(Screen.Home.name)
                })
        }
        composable(route = Screen.Onboarding.name) {
            OnboardingNotificationScreen(
                webViewViewModel = webViewViewModel,
                onChooseClick = {
                    navController.navigate(Screen.Home.name)
                })
        }
    }
}
