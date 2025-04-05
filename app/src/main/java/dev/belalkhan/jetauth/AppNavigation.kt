package dev.belalkhan.jetauth

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.belalkhan.jetauth.auth.ui.LoginView
import dev.belalkhan.jetauth.home.ui.HomeView

sealed class NavDestinations(val route: String) {
    object Login : NavDestinations("login")
    object Home : NavDestinations("home")
}

@Composable
fun AppNavigator(navController: NavHostController, authState: AuthState) {
    val startDestination = when (authState) {
        AuthState.Authenticated -> NavDestinations.Home.route
        else -> NavDestinations.Login.route
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(NavDestinations.Login.route) {
            LoginView(
                onLoginSuccess = {
                    navController.navigate(NavDestinations.Home.route) {
                        popUpTo(NavDestinations.Login.route) { inclusive = true }
                    }
                },
            )
        }

        composable(NavDestinations.Home.route) {
            HomeView(
                onLoggedOut = {
                    navController.navigate(NavDestinations.Login.route) {
                        popUpTo(0) // Clear everything from the back stack
                    }
                },
            )
        }
    }
}

