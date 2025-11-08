package fr.gouv.ami

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.gouv.ami.data.repository.getNotificationKey
import fr.gouv.ami.home.HomeScreen
import kotlinx.coroutines.flow.catch

//list of all screens
enum class Screen {
    Home
}

@Composable
fun HomeApp(navController: NavHostController = rememberNavController()) {

    val TAG = object {}.javaClass.enclosingClass?.simpleName ?: "AMI"

    //register to notifications
    LaunchedEffect(Unit) {
        val notificationKey = getNotificationKey()
        notificationKey
            .catch { e -> Log.d(TAG, e.toString()) }
            .collect { response ->
                val key = response.body()
                Log.d(TAG, "The key is $key")
            }
    }

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