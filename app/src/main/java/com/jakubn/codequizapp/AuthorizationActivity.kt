package com.jakubn.codequizapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.jakubn.codequizapp.navigation.Screen
import com.jakubn.codequizapp.theme.CodeQuizAppTheme
import com.jakubn.codequizapp.ui.authorization.LoginScreen
import com.jakubn.codequizapp.ui.authorization.RegistrationScreen
import com.jakubn.codequizapp.ui.authorization.WelcomeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthorizationActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navHostController = rememberNavController()

            CodeQuizAppTheme {
                NavHost(
                    navController = navHostController,
                    startDestination = Screen.AuthGraph.route
                ) {
                    navigation(
                        route = Screen.AuthGraph.route,
                        startDestination = Screen.Welcome.route
                    ) {
                        composable(route = Screen.Welcome.route) {
                            WelcomeScreen(navHostController) {
                                startActivity(Intent(applicationContext, MainActivity::class.java).apply {
                                    flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                })
                            }
                        }
                        composable(route = Screen.Registration.route) {
                            RegistrationScreen(navHostController)
                        }
                        composable(route = Screen.Login.route) {
                            LoginScreen {
                                startActivity(Intent(applicationContext, MainActivity::class.java).apply {
                                    flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}