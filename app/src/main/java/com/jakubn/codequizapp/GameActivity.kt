package com.jakubn.codequizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jakubn.codequizapp.navigation.Screen
import com.jakubn.codequizapp.theme.CodeQuizAppTheme
import com.jakubn.codequizapp.ui.game.createGame.CreateGameScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val action = intent.getStringExtra("action") ?: ""

        setContent {
            CodeQuizAppTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = getStartDestination(action)) {
                    composable(Screen.CreateGame.route) {
                        CreateGameScreen(navController)
                    }
                    composable(Screen.AvailableGameList.route) {
                        GamesListScreen()
                    }
                    composable("huh") {
//                        Huuh
                    }
                }
            }
        }
    }
}

fun getStartDestination(action: String): String {
    return if(action == "createGame") Screen.CreateGame.route else Screen.AvailableGameList.route
}

@Composable
fun GamesListScreen() {
    Text(text = "Here are the available games.")
}