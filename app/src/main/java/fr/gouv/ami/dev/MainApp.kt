package fr.gouv.ami.dev

import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.gouv.ami.dev.notifications.NotificationService
import fr.gouv.ami.dev.data.repository.getNotificationKey
import fr.gouv.ami.dev.home.HomeScreen
import kotlinx.coroutines.flow.catch

//list of all screens
enum class Screen {
    Home
}

@Composable
fun HomeApp(isNotification: Boolean, navController: NavHostController = rememberNavController()) {

    val TAG = object {}.javaClass.enclosingClass?.simpleName ?: "AMI"

    var startDestinationScreen = Screen.Home.name

    NavHost(
        navController = navController,
        startDestination = startDestinationScreen
    ) {
        composable(route = Screen.Home.name) {
            HomeScreen(isNotification)
        }
    }
}