package com.jakubn.codequizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.jakubn.codequizapp.theme.CodeQuizAppTheme
import com.jakubn.codequizapp.ui.createGame.CreateGameScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val action = intent.getStringExtra("type") ?: ""

        setContent {
            CodeQuizAppTheme {
                GameScreen(action = action)
            }
        }
    }
}

@Composable
fun GameScreen(action: String) {
    when (action) {
        "createGame" -> CreateGameScreen {  }
        "playGame" -> ActiveGamesListScreen()
        else -> {}
    }
}

@Composable
fun ActiveGamesListScreen() {
    Text(text = "Here are the active games.")
}
