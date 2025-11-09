package tn.esprit.labasniandroid

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import tn.esprit.labasniandroid.screens.LabasniForgotPasswordScreen
import tn.esprit.labasniandroid.screens.LabasniIntroScreen
import tn.esprit.labasniandroid.screens.LabasniLoginScreen
import tn.esprit.labasniandroid.screens.LabasniSignupScreen
import tn.esprit.labasniandroid.screens.home.LabasniMainScreen

sealed class LabasniDestination(val route: String) {
    data object Intro : LabasniDestination("intro")
    data object Login : LabasniDestination("login")
    data object Signup : LabasniDestination("signup")
    data object Forgot : LabasniDestination("forgot")
    data object Home : LabasniDestination("home")
}

@Composable
fun LabasniApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = LabasniDestination.Intro.route
    ) {
        composable(LabasniDestination.Intro.route) {
            LabasniIntroScreen(
                onLogin = {
                    navController.navigateSingleTop(LabasniDestination.Login.route)
                },
                onSignup = {
                    navController.navigateSingleTop(LabasniDestination.Signup.route)
                }
            )
        }

        composable(LabasniDestination.Login.route) {
            LabasniLoginScreen(
                onBack = { navController.popBackStack() },
                onForgotPassword = {
                    navController.navigateSingleTop(LabasniDestination.Forgot.route)
                },
                onCreateAccount = {
                    navController.navigateSingleTop(LabasniDestination.Signup.route)
                },
                onLoginSuccess = {
                    navController.navigateSingleTop(LabasniDestination.Home.route) {
                        popUpTo(LabasniDestination.Intro.route) { inclusive = false }
                    }
                }
            )
        }

        composable(LabasniDestination.Signup.route) {
            LabasniSignupScreen(
                onBack = { navController.popBackStack() },
                onAccountCreated = {
                    navController.navigateSingleTop(LabasniDestination.Home.route) {
                        popUpTo(LabasniDestination.Intro.route) { inclusive = false }
                    }
                }
            )
        }

        composable(LabasniDestination.Forgot.route) {
            LabasniForgotPasswordScreen(
                onBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigateSingleTop(LabasniDestination.Login.route) {
                        popUpTo(LabasniDestination.Forgot.route) { inclusive = true }
                    }
                }
            )
        }

        composable(LabasniDestination.Home.route) {
            LabasniMainScreen(
                onLogout = {
                    navController.navigate(LabasniDestination.Login.route) {
                        popUpTo(LabasniDestination.Intro.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

private fun NavHostController.navigateSingleTop(
    route: String,
    builder: (NavOptionsBuilder.() -> Unit)? = null
) {
    this.navigate(route) {
        launchSingleTop = true
        builder?.invoke(this)
    }
}
