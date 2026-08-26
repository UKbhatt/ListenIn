package com.example.listenin.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.listenin.data.MediaType
import com.example.listenin.ui.landing.LandingScreen
import com.example.listenin.ui.medialist.MediaListScreen
import com.example.listenin.ui.splash.SplashScreen

sealed class AppScreen(val route: String) {
    data object Splash : AppScreen("splash")
    data object Landing : AppScreen("landing")
    data object MediaList : AppScreen("media_list/{type}") {
        fun createRoute(type: MediaType) = "media_list/${type.route}"
    }
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
                onPlayAudio = { navController.navigate(AppScreen.MediaList.createRoute(MediaType.AUDIO)) },
                onPlayVideo = { navController.navigate(AppScreen.MediaList.createRoute(MediaType.VIDEO)) }
            )
        }
        composable(
            route = AppScreen.MediaList.route,
            arguments = listOf(navArgument("type") { type = NavType.StringType })
        ) { backStackEntry ->
            val mediaType = MediaType.fromRoute(backStackEntry.arguments?.getString("type"))
            MediaListScreen(
                type = mediaType,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
