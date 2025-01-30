package com.jakubn.codequizapp.ui.game.gameOver


import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.domain.model.User
import com.jakubn.codequizapp.theme.Typography

@Composable
fun GameOverScreen(user: User, gameId: String, navController: NavController, viewModel: GameOverViewModel = hiltViewModel()) {
    val gameState by viewModel.state.collectAsState()
    val lobbyState by viewModel.lobby.collectAsState()
    val context = LocalContext.current
    var showResult by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = lobbyState) {
        when (val currentState = lobbyState) {
            is CustomState.Success -> {
                if (viewModel.haveUsersFinishedGame()) {
                    if (!showResult) {
                        viewModel.finishGame(gameId, false)
                        viewModel.getUserScore(user)?.let { score ->
                            viewModel.updateUserData(user, score)
                        }
                        showResult = true
                    }
                }
            }
            is CustomState.Failure -> Toast.makeText(
                context,
                currentState.message,
                Toast.LENGTH_SHORT
            ).show()
            CustomState.Idle -> viewModel.getGameData(gameId)
            CustomState.Loading -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painterResource(R.drawable.background_auth),
                contentScale = ContentScale.FillBounds
            )
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Only show result when both players have finished
        if (showResult) {
            val hasWon = viewModel.hasCurrentUserWon(user)
            PlayersResultText(hasWon = hasWon)
        } else {
            Text("Waiting for other player...", style = Typography.titleLarge, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun PlayersResultText(hasWon: Boolean) {
    Text(style = Typography.titleLarge, text = if (hasWon) "You won!" else "You lost")
}

@Composable
fun ShowCircularProgressIndicator() {
    CircularProgressIndicator()
}