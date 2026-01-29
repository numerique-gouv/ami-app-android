package fr.gouv.ami

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.gouv.ami.dev.home.ReviewAppsScreen
import fr.gouv.ami.home.HomeScreen
import fr.gouv.ami.home.WebViewViewModel
import fr.gouv.ami.settings.SettingsScreen

//list of all screens
enum class Screen {
    Home,
    ReviewApp,
    Settings
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
                webViewViewModel = webViewViewModel
            )
        }
        composable(route = Screen.ReviewApp.name) {
            ReviewAppsScreen(
                onSelectedReviewApp = {
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
    }
}