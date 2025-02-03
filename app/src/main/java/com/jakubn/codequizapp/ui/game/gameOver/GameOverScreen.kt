package com.jakubn.codequizapp.ui.game.gameOver


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.domain.model.User
import com.jakubn.codequizapp.theme.Typography

@Composable
fun GameOverScreen(
    user: User,
    gameId: String,
    navController: NavController,
    viewModel: GameOverViewModel = hiltViewModel()
) {
    val gameState by viewModel.state.collectAsState()
    val lobbyState by viewModel.lobby.collectAsState()
    val context = LocalContext.current
    val haveUsersFinishedGame by viewModel.haveUsersFinishedGame.collectAsState()

    LaunchedEffect(key1 = lobbyState) {
        when (val currentState = lobbyState) {
            is CustomState.Success -> {
                if (viewModel.haveUsersFinishedGame()) {
                    viewModel.changeGameInProgressStatus(gameId, false)
                    viewModel.getUserScore(user)?.let { score ->
                        viewModel.updateUserData(user, score)
                    }
                }
            }

            is CustomState.Failure -> Toast.makeText(
                context,
                currentState.message,
                Toast.LENGTH_SHORT
            ).show()

            CustomState.Loading -> {}
            CustomState.Idle -> viewModel.getGameData(gameId)
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
        when (val currentState = gameState) {
            is CustomState.Success -> {
                if (haveUsersFinishedGame) {
                    val hasWon = viewModel.hasCurrentUserWon(user)
                    val winner = viewModel.determineWinner(currentState.result?.lobby)
                    val winnerPoints = viewModel.getWinnerPoints(currentState.result?.lobby)
                    val loser = viewModel.determineLoser(currentState.result?.lobby)
                    val loserPoints = viewModel.getLoserPoints(currentState.result?.lobby)
                    val correctAnswersQuantity = viewModel.getCorrectAnswersQuantity(currentState.result?.lobby)
                    val questionsQuantity = currentState.result?.questions?.size


                    // get rid of these null checks soon
                    if (winner != null) {
                        if (loser != null) {
                            if (winnerPoints != null) {
                                if (loserPoints != null) {
                                    if (correctAnswersQuantity != null) {
                                        if (questionsQuantity != null) {
                                            MainContainer(hasWon, winner, loser, winnerPoints, loserPoints, correctAnswersQuantity, questionsQuantity)
                                        }
                                    }
                                }
                            }
                        }
                    }

                } else {
                    Text(
                        "Waiting for other player...",
                        style = Typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                    CircularProgressIndicator()
                }
            }

            is CustomState.Failure -> ErrorState(currentState.message)
            CustomState.Loading -> {}
            CustomState.Idle -> {}
        }
    }
}

@Composable
fun MainContainer(
    hasWon: Boolean,
    winner: User,
    loser: User,
    winnerPoints: Int,
    loserPoints: Int,
    correctAnswersQuantity: Int,
    questionsQuantity: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        when (hasWon) {
            true -> WinnerContainer(winner, loser, winnerPoints, loserPoints, correctAnswersQuantity, questionsQuantity)

            false -> LoserContainer(winner, loser, winnerPoints, loserPoints, correctAnswersQuantity, questionsQuantity)
        }

    }
}

@Composable
fun WinnerContainer(
    winner: User,
    loser: User,
    winnerPoints: Int,
    loserPoints: Int,
    correctAnswersQuantity: Int,
    questionsQuantity: Int
) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = "You won!",
        style = Typography.titleLarge,
        textAlign = TextAlign.Center
    )
    val borderColors = arrayOf(
        0.20f to Color(0xFFA3FF0D),
        0.50f to Color(0xCCFFFFFF),
        1f to Color(0xFF032956)
    )

    val backgroundColors = arrayOf(
        0.50f to Color(0xCC344D67),
        0.85f to Color(0xFF061E3B)
    )

    Column(modifier = Modifier
        .border(5.dp, Brush.verticalGradient(colorStops = borderColors), RoundedCornerShape(20.dp))
        .clip(RoundedCornerShape(20.dp))
        .fillMaxWidth()
        .background(brush = Brush.verticalGradient(colorStops = backgroundColors))
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(intrinsicSize = IntrinsicSize.Max)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(150.dp)
                    .shadow(5.dp, shape = CircleShape)
                    .border(1.dp, Color.Black, CircleShape),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(winner.imageUri ?: R.drawable.sample_avatar)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.sample_avatar),
                contentDescription = stringResource(R.string.app_name),
                contentScale = ContentScale.Crop
            )
            Text(
                text = winner.name ?: "",
                style = Typography.bodyLarge,
                softWrap = true,
                color = Color.White
            )

            Text(
                text = "$winnerPoints points",
                style = Typography.titleSmall,
                softWrap = true,
                color = MaterialTheme.colorScheme.primary
            )

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(
                        text = "answered questions:",
                        style = Typography.bodyMedium,
                        softWrap = true,
                        color = Color.White
                    )

                    Text(
                        text = "$correctAnswersQuantity / $questionsQuantity",
                        style = Typography.bodyMedium,
                        softWrap = true,
                        color = MaterialTheme.colorScheme.primary
                    )

                }
            }

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(
                        text = "total games played:",
                        style = Typography.bodyMedium,
                        softWrap = true,
                        color = Color.White
                    )

                    Text(
                        text = "${winner.gamesPlayed}",
                        style = Typography.bodyMedium,
                        softWrap = true,
                        color = MaterialTheme.colorScheme.primary
                    )

                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp), // Adjust horizontal padding as needed
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(80.dp)
                        .shadow(5.dp, shape = CircleShape)
                        .border(1.dp, Color.Black, CircleShape),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(loser.imageUri ?: R.drawable.sample_avatar)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.sample_avatar),
                    contentDescription = stringResource(R.string.app_name),
                    contentScale = ContentScale.Crop
                )

                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = loser.name ?: "",
                        style = Typography.bodySmall,
                        softWrap = true,
                        color = Color.White,
                    )

                    Text(
                        text = "$loserPoints points",
                        style = Typography.bodySmall,
                        softWrap = true,
                        color = Color.White
                    )
                }
            }
        }
    }

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp), onClick = { }
    ) {
        Text(text = "show answers", textAlign = TextAlign.Center, softWrap = true)
    }
}

@Composable
fun LoserContainer(
    winner: User,
    loser: User,
    winnerPoints: Int,
    loserPoints: Int,
    correctAnswersQuantity: Int,
    questionsQuantity: Int
) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = "You lost!",
        style = Typography.titleLarge,
        textAlign = TextAlign.Center
    )

    val borderColors = arrayOf(
        0.20f to Color(0xFF032956),
        0.50f to Color(0xCCFFFFFF),
        1f to Color(0xFFA3FF0D)
    )

    val backgroundColors = arrayOf(
        0.50f to Color(0xCC061E3B),
        0.85f to Color(0xFF344D67)

    )

    Column(modifier = Modifier
        .border(5.dp, Brush.verticalGradient(colorStops = borderColors), RoundedCornerShape(20.dp))
        .clip(RoundedCornerShape(20.dp))
        .fillMaxWidth()
        .background(brush = Brush.verticalGradient(colorStops = backgroundColors))
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(intrinsicSize = IntrinsicSize.Max)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(150.dp)
                    .shadow(5.dp, shape = CircleShape)
                    .border(1.dp, Color.Black, CircleShape),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(winner.imageUri ?: R.drawable.sample_avatar)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.sample_avatar),
                contentDescription = stringResource(R.string.app_name),
                contentScale = ContentScale.Crop
            )
            Text(
                text = loser.name ?: "",
                style = Typography.bodyLarge,
                softWrap = true,
                color = Color.White
            )

            Text(
                text = "$loserPoints points",
                style = Typography.titleSmall,
                softWrap = true,
                color = Color.White
            )

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(
                        text = "answered questions:",
                        style = Typography.bodyMedium,
                        softWrap = true,
                        color = Color.White
                    )

                    Text(
                        text = "$correctAnswersQuantity / $questionsQuantity",
                        style = Typography.bodyMedium,
                        softWrap = true,
                        color = Color.White
                    )

                }
            }

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(
                        text = "total games played:",
                        style = Typography.bodyMedium,
                        softWrap = true,
                        color = Color.White
                    )

                    Text(
                        text = "${loser.gamesPlayed}",
                        style = Typography.bodyMedium,
                        softWrap = true,
                        color = Color.White
                    )

                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp), // Adjust horizontal padding as needed
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(80.dp)
                        .shadow(5.dp, shape = CircleShape)
                        .border(1.dp, Color.Black, CircleShape),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(loser.imageUri ?: R.drawable.sample_avatar)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.sample_avatar),
                    contentDescription = stringResource(R.string.app_name),
                    contentScale = ContentScale.Crop
                )

                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = winner.name ?: "",
                        style = Typography.bodySmall,
                        softWrap = true,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "$winnerPoints points",
                        style = Typography.bodySmall,
                        softWrap = true,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp), onClick = { }
    ) {
        Text(text = "show answers", textAlign = TextAlign.Center, softWrap = true)
    }
}

@Composable
fun ErrorState(errorMessage: String?) {
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