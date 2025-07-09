package com.jakubn.codequizapp.navigation

import com.jakubn.codequizapp.ui.authorization.LoginScreen
import com.jakubn.codequizapp.ui.authorization.RegistrationScreen
import com.jakubn.codequizapp.ui.authorization.WelcomeScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jakubn.codequizapp.domain.model.User


@Composable
fun AuthNavGraph(
    navController: NavHostController,
    onAuthSuccess: (User) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.AuthGraph.route
    ) {
        navigation(
            route = Screen.AuthGraph.route,
            startDestination = Screen.Welcome.route
        ) {
            composable(Screen.Welcome.route) {
                WelcomeScreen(navController) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            }
            composable(Screen.Registration.route) {
                RegistrationScreen(
                    navHostController = navController
                )
            }
            composable(Screen.Login.route) {
                LoginScreen(
                    logIn = { user -> onAuthSuccess(user) }
                )
            }
        }
    }
}