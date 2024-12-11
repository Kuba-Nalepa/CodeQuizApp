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
import com.jakubn.codequizapp.ui.MainScreen
import com.jakubn.codequizapp.ui.authorization.LoginScreen
import com.jakubn.codequizapp.ui.authorization.RegistrationScreen
import com.jakubn.codequizapp.ui.authorization.WelcomeScreen
import com.jakubn.codequizapp.ui.home.HomeScreen
import com.jakubn.codequizapp.ui.settings.SettingsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navHostController = rememberNavController()

            CodeQuizAppTheme {
                NavHost(
                    navController = navHostController,
                    startDestination = Screen.Main.route
                ) {
//                    navigation(
//                        route = Screen.Auth.route,
//                        startDestination = Screen.Welcome.route
//                    ) {
//                        composable(route = Screen.Welcome.route) {
//                            WelcomeScreen(navHostController)
//                        }
//                        composable(route = Screen.Registration.route) {
//                            RegistrationScreen(navHostController)
//                        }
//                        composable(route = Screen.Login.route) {
//                            LoginScreen(navHostController)
//                        }
//                    }

                    navigation(
                        route = Screen.Main.route,
                        startDestination = Screen.Home.route
                    ) {
                        composable(route = Screen.Home.route) {
                            MainScreen(navHostController) {
                                HomeScreen(createGame = {
                                    startActivity(
                                        Intent(
                                            applicationContext,
                                            GameActivity::class.java
                                        ).putExtra("action", "createGame")
                                    )
                                }, playGame = {
                                    startActivity(
                                        Intent(
                                            applicationContext,
                                            GameActivity::class.java
                                        ).putExtra("action", "playGame")
                                    )
                                })
                            }
                        }
                        composable(route = Screen.Leaderboard.route) {
                            MainScreen(navHostController) {

                            }
                        }
                        composable(route = Screen.Settings.route) {
                            MainScreen(navHostController) {
                                SettingsScreen(logOut = {
                                    startActivity(Intent(applicationContext, AuthorizationActivity::class.java).apply {
                                        flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    })
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}