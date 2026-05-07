package com.student.appmanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.student.appmanager.ui.screens.details.DetailScreen
import com.student.appmanager.ui.screens.home.HomeScreen

/**
 * Navigation graph for AppSweep.
 *
 * Defines all possible routes and their associated composables.
 *
 * Routes:
 * - "home" → Main app list screen (starting destination)
 * - "detail/{packageName}" → App detail screen for a specific app
 *
 * Navigation uses Jetpack Navigation Compose which provides:
 * - Type-safe route definitions
 * - Argument passing between screens
 * - Back stack management
 * - Deep link support (for future use)
 */
@Composable
fun AppSweepNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home",
    ) {
        // Home screen - the main app list
        composable("home") {
            HomeScreen(
                onNavigateToDetail = { packageName ->
                    navController.navigate("detail/$packageName")
                }
            )
        }

        // Detail screen - shows detailed info about one app
        composable(
            route = "detail/{packageName}",
            arguments = listOf(
                navArgument("packageName") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
            DetailScreen(
                packageName = packageName,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
