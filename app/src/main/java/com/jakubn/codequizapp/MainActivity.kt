package com.jakubn.codequizapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.navigation.Screen
import com.jakubn.codequizapp.theme.CodeQuizAppTheme
import com.jakubn.codequizapp.ui.MainScreen
import com.jakubn.codequizapp.ui.home.HomeScreen
import com.jakubn.codequizapp.ui.settings.SettingsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val currentUser by viewModel.currentUser.collectAsState()
            val navHostController = rememberNavController()
            when (val customStateUser = currentUser) {
                is CustomState.Success -> {
                    val user = customStateUser.result
                    CodeQuizAppTheme {
                        NavHost(
                            navController = navHostController,
                            startDestination = Screen.Main.route
                        ) {
                            navigation(
                                route = Screen.Main.route,
                                startDestination = Screen.Home.route
                            ) {
                                composable(route = Screen.Home.route) {
                                    MainScreen(navHostController) {
                                        HomeScreen(user, createGame = {
                                            startActivity(
                                                Intent(
                                                    applicationContext,
                                                    GameActivity::class.java
                                                ).putExtra("action", "createGame")
                                                    .putExtra("user", user)
                                            )
                                        }, playGame = {
                                            startActivity(
                                                Intent(
                                                    applicationContext,
                                                    GameActivity::class.java
                                                ).putExtra("action", "playGame")
                                                    .putExtra("user", user)
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
                                            startActivity(
                                                Intent(
                                                    applicationContext,
                                                    AuthorizationActivity::class.java
                                                ).apply {
                                                    flags =
                                                        (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                })
                                        })
                                    }
                                }
                            }
                        }
                    }
                }

                is CustomState.Failure -> {

                    CodeQuizAppTheme {
                        Text(text = "Error: ${customStateUser.message}")
                    }
                }

                CustomState.Loading -> {
                    CodeQuizAppTheme {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                        }
                    }
                }

                CustomState.Idle -> {
                    CodeQuizAppTheme {
                        Text(text = "Idle State")
                    }
                }

            }
        }
    }
}