package com.example.listenin.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.listenin.ui.landing.LandingScreen
import com.example.listenin.ui.splash.SplashScreen

sealed class AppScreen(val route: String) {
    data object Splash : AppScreen("splash")
    data object Landing : AppScreen("landing")
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreen.Splash.route
    ) {
        composable(AppScreen.Splash.route) {
            SplashScreen {
                navController.navigate(AppScreen.Landing.route) {
                    popUpTo(AppScreen.Splash.route) { inclusive = true }
                }
            }
        }

        composable(AppScreen.Landing.route) {
            LandingScreen(
                onGetStartedClick = {
                    // hook for next screen later
                },
                onDemoClick = {
                    // hook for demo actions later
                }
            )
        }
    }
}
