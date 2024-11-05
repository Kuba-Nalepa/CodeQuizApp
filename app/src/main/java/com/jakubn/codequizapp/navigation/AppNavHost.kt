package com.jakubn.codequizapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jakubn.codequizapp.ui.welcome.WelcomeScreen


@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Navigate.UserProfileScreen.route) {

        composable(route = Navigate.UserProfileScreen.route) {
            WelcomeScreen()
        }
    }
}