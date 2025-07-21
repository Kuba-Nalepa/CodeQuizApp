package com.jakubn.codequizapp.navigation

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jakubn.codequizapp.model.User
import com.jakubn.codequizapp.ui.MainScreen
import com.jakubn.codequizapp.ui.home.HomeScreen
import com.jakubn.codequizapp.ui.settings.editProfile.UserProfileEditScreen
import com.jakubn.codequizapp.ui.settings.SettingsScreen
import com.jakubn.codequizapp.GameActivity
import com.jakubn.codequizapp.ui.leaderboard.LeaderboardScreen

@Composable
fun MainNavGraph(
    navController: NavHostController,
    user: User,
    appContext: Context,
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.MainGraph.route
    ) {
        navigation(
            route = Screen.MainGraph.route,
            startDestination = Screen.Home.route
        ) {
            composable(route = Screen.Home.route) {
                MainScreen(navController) {
                    HomeScreen(
                        createGame = {
                            appContext.startActivity(
                                Intent(
                                    appContext,
                                    GameActivity::class.java
                                ).putExtra("action", "createGame")
                                    .putExtra("user", user)
                            )
                        },
                        playGame = {
                            appContext.startActivity(
                                Intent(
                                    appContext,
                                    GameActivity::class.java
                                ).putExtra("action", "playGame")
                                    .putExtra("user", user)
                            )
                        }
                    )
                }
            }

            composable(route = Screen.Leaderboard.route) {
                MainScreen(navController) {
                    LeaderboardScreen()
                }
            }

            navigation(
                route = Screen.SettingsGraph.route,
                startDestination = Screen.Settings.route
            ) {
                composable(route = Screen.Settings.route) {
                    MainScreen(navController) {
                        SettingsScreen(
                            user = user,
                            navController = navController,
                            onLogoutConfirmed = { onLogout() }
                        )
                    }
                }
                composable(route = Screen.UserProfileEdit.route) {
                    UserProfileEditScreen(
                        navController = navController
                    )
                }
            }
        }
    }
}