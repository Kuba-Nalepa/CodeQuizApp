package com.jakubn.codequizapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.jakubn.codequizapp.ui.authorization.LoginScreen
import com.jakubn.codequizapp.ui.authorization.RegistrationScreen
import com.jakubn.codequizapp.ui.authorization.WelcomeScreen
import com.jakubn.codequizapp.ui.home.HomeScreen


@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Navigate.Auth.route) {

        navigation(route = Navigate.Auth.route, startDestination = Navigate.Welcome.route) {

            composable(route = Navigate.Welcome.route) {
                WelcomeScreen(navController)
            }

            composable(route = Navigate.Registration.route) {
                RegistrationScreen()
            }
            composable(route = Navigate.Login.route) {
                LoginScreen(navController)
            }

        }

        navigation(route = Navigate.Main.route, startDestination = Navigate.Home.route) {

            composable(route = Navigate.Home.route) {
                HomeScreen(navController)
            }
        }
    }
}