package tn.esprit.labasniandroid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import tn.esprit.labasniandroid.ui.screen.auth.forgotpassword.ForgotPasswordView
import tn.esprit.labasniandroid.ui.screen.intro.IntroView
import tn.esprit.labasniandroid.ui.screen.auth.login.LoginView
import tn.esprit.labasniandroid.ui.screen.auth.signup.SignupView
import tn.esprit.labasniandroid.ui.screen.home.MainScreen
import tn.esprit.labasniandroid.utils.TokenManager

sealed class LabasniDestination(val route: String) {
    data object Intro : LabasniDestination("intro")
    data object Login : LabasniDestination("login")
    data object Signup : LabasniDestination("signup")
    data object Forgot : LabasniDestination("forgot")
    data object Home : LabasniDestination("home")
}

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun LabasniApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    
    // ✨ NOUVEAU : Vérifier si l'utilisateur est connecté au démarrage (comme iOS)
    var isLoggedIn by remember { mutableStateOf<Boolean?>(null) }
    
    LaunchedEffect(Unit) {
        // Vérifier le token et l'userId au démarrage
        val token = TokenManager.getToken(context)
        val userId = TokenManager.getUserId(context)
        isLoggedIn = !token.isNullOrBlank() && !userId.isNullOrBlank()
    }
    
    // Déterminer la destination de départ selon l'état de connexion
    val startDestination = when (isLoggedIn) {
        true -> LabasniDestination.Home.route // Utilisateur connecté → aller directement à Home
        false -> LabasniDestination.Intro.route // Utilisateur non connecté → afficher Intro
        null -> LabasniDestination.Intro.route // En attente de vérification → afficher Intro par défaut
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(LabasniDestination.Intro.route) {
            IntroView(
                onLogin = {
                    navController.navigateSingleTop(LabasniDestination.Login.route)
                },
                onSignup = {
                    navController.navigateSingleTop(LabasniDestination.Signup.route)
                }
            )
        }

        composable(LabasniDestination.Login.route) {
            LoginView(
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
            SignupView(
                onBack = { navController.popBackStack() },
                onAccountCreated = {
                    navController.navigateSingleTop(LabasniDestination.Home.route) {
                        popUpTo(LabasniDestination.Intro.route) { inclusive = false }
                    }
                }
            )
        }

        composable(LabasniDestination.Forgot.route) {
            ForgotPasswordView(
                onBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigateSingleTop(LabasniDestination.Login.route) {
                        popUpTo(LabasniDestination.Forgot.route) { inclusive = true }
                    }
                }
            )
        }

        composable(LabasniDestination.Home.route) {
            MainScreen(
                onLogout = {
                    // ✨ NOUVEAU : Nettoyer le token et rediriger vers Login (comme iOS)
                    TokenManager.clearToken(context)
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
