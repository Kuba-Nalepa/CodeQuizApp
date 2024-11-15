package com.jakubn.codequizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.jakubn.codequizapp.navigation.Screen
import com.jakubn.codequizapp.theme.CodeQuizAppTheme
import com.jakubn.codequizapp.ui.authorization.LoginScreen
import com.jakubn.codequizapp.ui.authorization.RegistrationScreen
import com.jakubn.codequizapp.ui.authorization.WelcomeScreen
import com.jakubn.codequizapp.ui.home.HomeScreen
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
                    startDestination = Screen.Auth.route
                ) {
                    navigation(
                        route = Screen.Auth.route,
                        startDestination = Screen.Welcome.route
                    ) {
                        composable(route = Screen.Welcome.route) {
                            WelcomeScreen(navHostController)
                        }
                        composable(route = Screen.Registration.route) {
                            RegistrationScreen()
                        }
                        composable(route = Screen.Login.route) {
                            LoginScreen(navHostController)
                        }
                    }

                    navigation(
                        route = Screen.Main.route,
                        startDestination = Screen.Home.route
                    ) {
                        composable(route = Screen.Home.route) {
                            MainScreen(navHostController) {
                                HomeScreen(navHostController)
                            }
                        }
                        composable(route = Screen.Leaderboard.route) {
                            MainScreen(navHostController) {

                            }
                        }
                        composable(route = Screen.MyProfile.route) {
                            MainScreen(navHostController) {

                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CodeQuizAppTheme {

    }
}