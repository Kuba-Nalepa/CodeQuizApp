package com.jakubn.codequizapp.ui.game.gameOver


import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.domain.model.CorrectAnswers
import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.domain.model.Question
import com.jakubn.codequizapp.theme.Typography
import kotlin.reflect.full.memberProperties

@Composable
fun GameOverScreen(gameId: String, navController: NavController, viewModel: GameOverViewModel = hiltViewModel()) {
    val gameState by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(gameState) {
        when(val currentState = gameState) {
            is CustomState.Success -> {

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


    }
}

@Composable
fun PlayersResultText(hasWon: Boolean) {
    Text(style = Typography.titleLarge, text = if(hasWon) "You won!" else "You lost")
}