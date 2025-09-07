package com.jakubn.codequizapp.navigation

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.jakubn.codequizapp.model.User
import com.jakubn.codequizapp.ui.MainScreen
import com.jakubn.codequizapp.ui.home.HomeScreen
import com.jakubn.codequizapp.ui.settings.editProfile.UserProfileEditScreen
import com.jakubn.codequizapp.ui.settings.SettingsScreen
import com.jakubn.codequizapp.GameActivity
import com.jakubn.codequizapp.model.Friend
import com.jakubn.codequizapp.ui.chat.ChatScreen
import com.jakubn.codequizapp.ui.leaderboard.LeaderboardScreen
import com.jakubn.codequizapp.ui.notifications.NotificationsScreen

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
                        },
                        navigateToChat = { friend ->
                            navController.navigate(
                                Screen.Chat.createRoute(
                                    friendUid = friend.uid ?: "Unknown",
                                    friendName = friend.name ?: "Unknown"
                                )
                            )
                        }
                    )
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

            composable(route = Screen.Leaderboard.route) {
                MainScreen(navController) {
                    LeaderboardScreen(
                        user = user,
                        navigateToChat = { friend ->
                            navController.navigate(
                                Screen.Chat.createRoute(
                                    friendUid = friend.uid ?: "Unknown",
                                    friendName = friend.name ?: "Unknown"
                                )
                            )
                        }
                    )
                }
            }

            composable(
                route = Screen.Chat.route,
                arguments = listOf(
                    navArgument("friendUid") { type = NavType.StringType },
                    navArgument("friendName") { type = NavType.StringType }
                )
            ) { navBackStackEntry ->
                val friendUid = navBackStackEntry.arguments?.getString("friendUid")
                val friendName = navBackStackEntry.arguments?.getString("friendName")


                if (friendUid != "Unknown" && friendName != "Unknown") {
                    val friend = Friend(uid = friendUid, name = friendName, imageUri = null)
                    MainScreen(navController) {
                        ChatScreen(
                            user = user,
                            friend = friend
                        )
                    }
                } else {
                    Toast.makeText(appContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            }

            composable(
                route = Screen.Notifications.route,
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")
                if (userId != null) {
                    NotificationsScreen(userId = userId)
                }
            }
        }
    }
}