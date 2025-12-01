package fr.gouv.ami

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.gouv.ami.dev.home.ReviewAppsScreen
import fr.gouv.ami.home.HomeScreen

//list of all screens
enum class Screen {
    Home,
    ReviewApp
}

@Composable
fun HomeApp(navController: NavHostController = rememberNavController()) {

    val TAG = object {}.javaClass.enclosingClass?.simpleName ?: "AMI"

    var startDestinationScreen = Screen.ReviewApp.name

    NavHost(
        navController = navController,
        startDestination = startDestinationScreen
    ) {
        composable(route = Screen.Home.name) {
            HomeScreen()
        }
        composable(route = Screen.ReviewApp.name) {
            ReviewAppsScreen(
                onSelectedReviewApp = {
                    navController.navigate(Screen.Home.name)
                }
            )
        }
    }
}