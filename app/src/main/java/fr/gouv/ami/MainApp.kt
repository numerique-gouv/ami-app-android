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
import fr.gouv.ami.home.FranceConnectionScreen
import fr.gouv.ami.home.HomeScreen
import fr.gouv.ami.home.WebViewViewModel
import fr.gouv.ami.home.WebViewViewModelFactory
import fr.gouv.ami.settings.SettingsScreen
import fr.gouv.ami.settings.OnboardingNotificationScreen
import fr.gouv.ami.utils.storage.LowStorageManager
import fr.gouv.ami.webview.WebViewType
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
fun HomeApp(
    navController: NavHostController = rememberNavController(),
    pendingUrl: String? = null
) {

    val TAG = object {}.javaClass.enclosingClass?.simpleName ?: "AMI"
    val destinationUrlKey = "destinationUrl"
    val webViewTypeKey = "webViewType"


    val webViewViewModel = viewModel<WebViewViewModel>(
        factory = WebViewViewModelFactory(WebViewType.HOME)
    )
    val storage = LowStorageManager(LocalContext.current)
    var isConnected by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        val token = storage.getBearerToken()
        if (token != null) {
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
                        storage.clearBearer()
                    }
                }
        }
    }

    if (pendingUrl != null) {
        Log.d(TAG, "App has been opened from clicking on a push notification.")
        //webViewViewModel.currentUrl = pendingUrl
    }

    val connectionDestination =
        if (isConnected)
            "${Screen.Home.name}?webViewType=${WebViewType.HOME.name}"
        else
            Screen.FranceConnection.name

    var startDestinationScreen = connectionDestination

    if (BuildConfig.FLAVOR == "staging" && pendingUrl == null) {
        startDestinationScreen = Screen.ReviewApp.name
    }

    NavHost(
        navController = navController,
        startDestination = startDestinationScreen
    ) {
        composable(
            route = "${Screen.Home.name}?${webViewTypeKey}={${webViewTypeKey}}&${destinationUrlKey}={${destinationUrlKey}}",
            arguments = listOf(
                navArgument(webViewTypeKey) {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument(destinationUrlKey) {
                    type = NavType.StringType
                    nullable = true
                })

        ) { backStackEntry ->
            val webViewType = WebViewType.valueOf(
                Uri.decode(
                    backStackEntry.arguments?.getString(webViewTypeKey) ?: WebViewType.HOME.name
                )
            )

            val destinationUrl = Uri.decode(
                backStackEntry.arguments?.getString(destinationUrlKey) ?: baseUrl
            )


            HomeScreen(
                startUrl = destinationUrl,
                webViewType = webViewType,
                navigate = { screen, destinationUrl, destinationWebViewType ->
                    navController.navigate(route = "${screen.name}?${destinationUrlKey}=${destinationUrl}&${webViewTypeKey}=${destinationWebViewType}") {
                        if (webViewType == WebViewType.CONNECTION) {
                            popUpTo(navController.currentDestination!!.id) {
                                inclusive = true
                            }
                        }
                    }
                },
                backButton = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.ReviewApp.name) {
            ReviewAppsScreen(
                onSelectedReviewApp = {
                    navController.navigate(connectionDestination)
                }
            )
        }
        composable(route = Screen.FranceConnection.name) {
            FranceConnectionScreen(
                onFcClick = {
                    val url = "${baseUrl}login-france-connect"
                    navController.navigate(
                        route = "${Screen.Home.name}?${destinationUrlKey}=${Uri.encode(url)}&${webViewTypeKey}=${WebViewType.CONNECTION.name}"
                    )
                })
        }
        composable(route = Screen.Settings.name) {
            SettingsScreen(
                onBackButton = {
                    navController.popBackStack()
                },
                webViewViewModel = webViewViewModel,
            )
        }
        composable(route = Screen.Onboarding.name) {
            OnboardingNotificationScreen(
                webViewViewModel = webViewViewModel,
                onChooseClick = {
                    navController.navigate(
                        route = "${Screen.Home.name}?${webViewTypeKey}=${WebViewType.HOME.name}"
                    )
                }
            )
        }
    }
}
