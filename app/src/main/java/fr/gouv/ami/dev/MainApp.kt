package fr.gouv.ami.dev

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.gouv.ami.dev.home.HomeScreen

//list of all screens
enum class Screen {
    Home
}

@Composable
fun HomeApp(navController: NavHostController = rememberNavController()) {

    var startDestinationScreen = Screen.Home.name

    NavHost(
        navController = navController,
        startDestination = startDestinationScreen
    ) {
        composable(route = Screen.Home.name) {
            HomeScreen()
        }
    }
}