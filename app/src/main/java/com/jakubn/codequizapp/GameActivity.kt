package com.jakubn.codequizapp

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jakubn.codequizapp.domain.model.User
import com.jakubn.codequizapp.navigation.Screen
import com.jakubn.codequizapp.theme.CodeQuizAppTheme
import com.jakubn.codequizapp.ui.game.QuizScreen
import com.jakubn.codequizapp.ui.game.availableGames.AvailableGameListScreen
import com.jakubn.codequizapp.ui.game.createGame.CreateGameScreen
import com.jakubn.codequizapp.ui.game.gameOver.GameOverScreen
import com.jakubn.codequizapp.ui.game.lobby.LobbyScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameActivity : ComponentActivity() {

    private inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val action = intent.getStringExtra("action") ?: ""
        val user: User? = intent.parcelable("user")

        setContent {
            CodeQuizAppTheme {
                val navController = rememberNavController()
                if (user == null) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text("User data is empty")
                    }
                } else {
                    NavHost(
                        navController = navController,
                        startDestination = getStartDestination(action)
                    ) {
                        composable(Screen.CreateGame.route) {
                            CreateGameScreen(user, navController)
                        }
                        composable(Screen.AvailableGameList.route) {
                            AvailableGameListScreen(user, navController)
                        }
                        composable(Screen.Lobby.route + "/{gameId}") { backStackEntry ->

                            backStackEntry.arguments?.getString("gameId")
                                ?.let { LobbyScreen(user, navController, it) }
                        }

                        composable(Screen.Quiz.route + "/{gameId}") { backStackEntry ->

                            backStackEntry.arguments?.getString("gameId")
                                ?.let { QuizScreen(user, navController, it) }
                        }

                        composable(Screen.GameOver.route + "/{gameId}") { backStackEntry ->
                            val gameId = backStackEntry.arguments?.getString("gameId")
                            gameId?.let { id ->
                                GameOverScreen(user, id, navController)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getStartDestination(action: String): String {
    return if (action == "createGame") Screen.CreateGame.route else Screen.AvailableGameList.route
}