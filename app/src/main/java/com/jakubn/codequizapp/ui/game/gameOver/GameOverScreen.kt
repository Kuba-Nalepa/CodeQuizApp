package com.jakubn.codequizapp.ui.game.gameOver


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.domain.model.GameResult
import com.jakubn.codequizapp.domain.model.User
import com.jakubn.codequizapp.theme.Typography
import com.jakubn.codequizapp.ui.game.availableGames.LoadingState

@Composable
fun GameOverScreen(
    user: User,
    gameId: String,
    navController: NavController,
    viewModel: GameOverViewModel = hiltViewModel()
) {
    val loadingState by viewModel.state.collectAsState()
    val gameResult by viewModel.gameResult.collectAsState()
    val bothUsersFinished by viewModel.bothUsersFinished.collectAsState()

    LaunchedEffect(gameId) {
        viewModel.getGameData(gameId, user)
    }

    LaunchedEffect(gameResult) {
        gameResult?.let {
            viewModel.handleGameCleanup(gameId)
            viewModel.updateUserData(user, calculateUserScore(it, user))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .paint(
                painterResource(R.drawable.background_auth),
                contentScale = ContentScale.FillBounds
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            !bothUsersFinished -> WaitingForOpponent()
            loadingState is CustomState.Loading -> LoadingState()
            loadingState is CustomState.Failure -> FailureState((loadingState as CustomState.Failure).message)
            gameResult != null -> GameResultContent(gameResult!!, user)
            else -> FailureState("No game results available")
        }
    }
}

@Composable
private fun GameResultContent(result: GameResult, currentUser: User) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        when (result) {
            is GameResult.Win -> WinnerContainer(result)
            is GameResult.Lose -> LoserContainer(result)
            is GameResult.Tie -> TieContainer(result, currentUser)
        }
    }

}

@Composable
private fun WinnerContainer(result: GameResult.Win) {
    val borderColors = arrayOf(
        0.20f to Color(0xFFA3FF0D),
        0.50f to Color(0xCCFFFFFF),
        1f to Color(0xFF032956)
    )

    val backgroundColors = arrayOf(
        0.50f to Color(0xCC344D67),
        0.85f to Color(0xFF061E3B)
    )

    Text(modifier = Modifier.fillMaxWidth(), text = "You won!", style = Typography.titleLarge, textAlign = TextAlign.Center)

    Column(
        modifier = Modifier
            .border(
                5.dp,
                Brush.verticalGradient(colorStops = borderColors),
                RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .height(intrinsicSize = IntrinsicSize.Min)
            .background(brush = Brush.verticalGradient(colorStops = backgroundColors))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {

        UserProfileSection(user = result.winner, points = result.winnerPoints)
        GameStatsSection(
            correctAnswers = result.correctAnswers,
            totalQuestions = result.totalQuestions,
            gamesPlayed = result.winner.gamesPlayed,
            color = MaterialTheme.colorScheme.primary
        )
        OpponentSection(user = result.loser, points = result.loserPoints)
    }

    ActionButton() { /* Handle action */ }

}

@Composable
private fun LoserContainer(result: GameResult.Lose) {
    val borderColors = arrayOf(
        0.20f to Color(0xFF032956),
        0.50f to Color(0xCCFFFFFF),
        1f to Color(0xFFA3FF0D)
    )

    val backgroundColors = arrayOf(
        0.50f to Color(0xCC061E3B),
        0.85f to Color(0xFF344D67)

    )

    Text(modifier = Modifier.fillMaxWidth(), text = "You lost!", style = Typography.titleLarge, textAlign = TextAlign.Center)

    Column(
        modifier = Modifier
            .border(
                5.dp,
                Brush.verticalGradient(colorStops = borderColors),
                RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .background(brush = Brush.verticalGradient(colorStops = backgroundColors))
            .padding(20.dp)
    ) {

        UserProfileSection(user = result.loser, points = result.loserPoints)
        GameStatsSection(
            correctAnswers = result.correctAnswers,
            totalQuestions = result.totalQuestions,
            gamesPlayed = result.loser.gamesPlayed,
            color = Color.White
        )
        OpponentSection(user = result.winner, points = result.winnerPoints)
    }
    ActionButton() { /* Handle action */ }
}

@Composable
private fun TieContainer(result: GameResult.Tie, currentUser: User) {
    val borderColors = arrayOf(
        0.20f to Color(0xFF032956),
        0.50f to Color(0xCCFFFFFF),
        1f to Color(0xFF032956)
    )

    val backgroundColors = arrayOf(
        0.50f to Color(0xCC344D67),
        0.85f to Color(0xFF061E3B)
    )

    Text(modifier = Modifier.fillMaxWidth(), text = "It's a tie!", style = Typography.titleLarge, textAlign = TextAlign.Center)

    Column(
        modifier = Modifier
            .border(
                5.dp,
                Brush.verticalGradient(colorStops = borderColors),
                RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .background(brush = Brush.verticalGradient(colorStops = backgroundColors))
            .padding(24.dp)
    ) {
        UserProfileSection(
            user = if (result.firstUser.uid == currentUser.uid) result.firstUser else result.secondUser,
            points = if (result.firstUser.uid == currentUser.uid) result.firstUserPoints else result.secondUserPoints
        )

        Spacer(Modifier.size(50.dp))

        OpponentSection(
            user = if (result.firstUser.uid == currentUser.uid) result.firstUser else result.secondUser,
            points = if (result.secondUser.uid == currentUser.uid) result.secondUserPoints else result.firstUserPoints
        )
    }
    ActionButton() { /* Handle action */ }

}

@Composable
private fun OpponentSection(user: User, points: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Opponent Image
        AsyncImage(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .border(1.dp, Color.LightGray, CircleShape),
            model = user.imageUri ?: R.drawable.sample_avatar,
            contentDescription = "Opponent profile",
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Opponent",
                style = Typography.labelSmall,
                color = Color.LightGray
            )
            Text(
                modifier = Modifier.padding(top = 5.dp),
                text = user.name ?: "Unknown Player",
                style = Typography.bodyMedium,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "$points points",
                style = Typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun WaitingForOpponent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Waiting for opponent to finish...",
            style = Typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp),
            color = Color.White
        )
        CircularProgressIndicator(
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun UserProfileSection(user: User, points: Int) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Black, CircleShape),
            model = user.imageUri ?: R.drawable.sample_avatar,
            contentDescription = "User profile"
        )
        Text(
            modifier = Modifier.padding(top = 15.dp),
            text = user.name ?: "",
            style = Typography.titleMedium,
            color = Color.White
        )
        Text(
            modifier = Modifier.padding(top = 5.dp),
            text = "$points points",
            style = Typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun GameStatsSection(
    correctAnswers: Int,
    totalQuestions: Int,
    gamesPlayed: Int,
    color: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem("Correct Answers", "$correctAnswers / $totalQuestions", color)
        StatItem("Total Games Played", gamesPlayed.toString(), color)
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(bottom = 5.dp),
            text = label,
            style = Typography.bodyMedium,
            color = Color.White
        )
        Text(text = value, style = Typography.bodyMedium, color = color)
    }
}

@Composable
private fun ActionButton(onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        onClick = onClick
    ) {
        Text("show answers")
    }
}

@Composable
fun FailureState(errorMessage: String?) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = errorMessage ?: "Unknown error", color = Color.White)
    }
}

private fun calculateUserScore(result: GameResult, currentUser: User): Int {
    return when (result) {
        is GameResult.Win -> {
            when (currentUser.uid) {
                result.winner.uid -> result.winnerPoints
                result.loser.uid -> result.loserPoints
                else -> throw IllegalStateException("User not part of this game result")
            }
        }

        is GameResult.Lose -> {
            when (currentUser.uid) {
                result.loser.uid -> result.loserPoints
                result.winner.uid -> result.winnerPoints
                else -> throw IllegalStateException("User not part of this game result")
            }
        }

        is GameResult.Tie -> {
            when (currentUser.uid) {
                result.firstUser.uid -> result.firstUserPoints
                result.secondUser.uid -> result.secondUserPoints
                else -> throw IllegalStateException("User not part of this game result")
            }
        }
    }
}