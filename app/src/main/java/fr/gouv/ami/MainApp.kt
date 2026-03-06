package fr.gouv.ami

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import fr.gouv.ami.api.baseUrl
import fr.gouv.ami.data.repository.checkAuth
import fr.gouv.ami.dev.home.ReviewAppsScreen
import fr.gouv.ami.home.FranceConnexionScreen
import fr.gouv.ami.home.HomeScreen
import fr.gouv.ami.home.WebViewViewModel
import fr.gouv.ami.settings.SettingsScreen
import fr.gouv.ami.settings.OnboardingNotificationScreen
import fr.gouv.ami.utils.ManagerLocalStorage
import kotlinx.coroutines.flow.catch

//list of all screens
enum class Screen {
    Home,
    ReviewApp,
    Settings,
    Onboarding,
    FranceConnection
}

@Composable
fun HomeApp(navController: NavHostController = rememberNavController()) {

    val TAG = object {}.javaClass.enclosingClass?.simpleName ?: "AMI"
    val webViewViewModel = viewModel<WebViewViewModel>()
    val storage = ManagerLocalStorage(LocalContext.current)
    var isConnected by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        storage.getBearer()?.let { token ->
            val authenticationFlow = checkAuth(token)
            authenticationFlow
                .catch { e ->
                    Log.e(TAG, "Error", e)
                }
                .collect { response ->
                    if (response.isSuccessful) {
                        isConnected = true
                        Log.d(TAG, "Successfully: user is connected with bearer $token")
                    } else {
                        Log.e(
                            TAG,
                            "Error: ${response.code()} - ${response.message()}"
                        )
                    }
                }
        }
    }

    val connexionDestination =
        if (isConnected)
            Screen.Home.name
        else
            Screen.FranceConnection.name

    var startDestinationScreen = connexionDestination
    if (BuildConfig.FLAVOR == "staging") {
        startDestinationScreen = Screen.ReviewApp.name
    }

    NavHost(
        navController = navController,
        startDestination = startDestinationScreen
    ) {
        composable(
            route = "${Screen.Home.name}?url={url}",
            arguments = listOf(navArgument("url") {
                type = NavType.StringType
                nullable = true
            })
        ) { backStackEntry ->
            val url = Uri.decode(
                backStackEntry.arguments?.getString("url") ?: baseUrl
            )

            HomeScreen(
                goSettings = {
                    navController.navigate(Screen.Settings.name)
                },
                goOnboarding = {
                    navController.navigate(Screen.Onboarding.name)
                },
                webViewViewModel = webViewViewModel,
                goAuth = {
                    navController.navigate(Screen.FranceConnection.name)
                },
                startUrl = url,
            )
        }

        composable(route = Screen.ReviewApp.name) {
            ReviewAppsScreen(
                onSelectedReviewApp = {
                    navController.navigate(connexionDestination)
                }
            )
        }
        composable(route = Screen.FranceConnection.name) {
            FranceConnexionScreen(
                onFcClick = {
                    val url = "$baseUrl/login-france-connect"
                    navController.navigate(
                        route = "${Screen.Home.name}?url=${Uri.encode(url)}"
                    )
                })
        }
        composable(route = Screen.Settings.name) {
            SettingsScreen(
                onBackButton = {
                    navController.navigate(Screen.Home.name)
                },
                webViewViewModel = webViewViewModel
            )
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