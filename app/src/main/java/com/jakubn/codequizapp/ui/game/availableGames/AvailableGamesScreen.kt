package com.jakubn.codequizapp.ui.game.availableGames

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.model.Game
import com.jakubn.codequizapp.model.User
import com.jakubn.codequizapp.navigation.Screen
import com.jakubn.codequizapp.theme.Typography

@Composable
fun AvailableGameListScreen(
    user: User,
    navController: NavController,
    viewModel: AvailableGamesViewModel = hiltViewModel()
) {
    val gamesState by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getGamesList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painterResource(R.drawable.background_auth),
                contentScale = ContentScale.FillBounds
            )
    ) {
        when (val currentGamesState = gamesState) {
            is CustomState.Success -> {
                SetGamesList(
                    games = currentGamesState.result,
                    onClick = { gameId ->
                        viewModel.addUserToLobby(gameId, user)
                        navController.navigate(Screen.Lobby.route + "/$gameId")
                    }
                )
            }
            is CustomState.Failure -> ErrorState(currentGamesState.message)
            CustomState.Loading -> LoadingState()
            CustomState.Idle -> LoadingState()
        }
    }
}

@Composable
fun SetGamesList(
    games: List<Game>?,
    onClick: ((gameId: String) -> Unit)?
) {
    if (games.isNullOrEmpty()) {
        EmptyState()
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(games) { game ->
                GamesListItem(
                    data = game,
                    onClick = {
                         onClick?.invoke(game.gameId)
                    }
                )
            }
        }
    }
}

@Composable
fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "There are no available games right now",
            textAlign = TextAlign.Center,
            style = Typography.bodyMedium
        )
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

@Composable
fun GamesListItem(data: Game, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 20.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .background(Color(0x80FFFFFF))
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FounderInfo(data)
        GameDetails(data)
    }
}

@Composable
fun FounderInfo(data: Game) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(50.dp)
                    .shadow(5.dp, CircleShape)
                    .border(1.dp, Color.Black, CircleShape),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(data.lobby?.founder?.imageUri ?: R.drawable.sample_avatar)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.sample_avatar),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Text(text = data.lobby?.founder?.name ?: "Unknown")
        }

        data.category?.let {
            Text(
                modifier = Modifier
                    .border(1.dp, Color(0xFF006134), RoundedCornerShape(5.dp))
                    .clip(RoundedCornerShape(5.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(4.dp),
                text = it,
                style = Typography.labelMedium,
                color = Color.Black
            )
        }
    }
}

@Composable
fun GameDetails(data: Game) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(15.dp))
            .clip(RoundedCornerShape(15.dp))
            .background(Color(0x4DFFFFFF))
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        DetailRow(
            iconRes = R.drawable.ic_players,
            label = "Players:",
            value = if (data.lobby?.member == null) "1/2" else "2/2",
            valueColor = if (data.lobby?.member == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )

        DetailRow(
            iconRes = R.drawable.ic_quantity,
            label = "Question quantity:",
            value = "${data.questions?.size}"
        )

        DetailRow(
            iconRes = R.drawable.ic_duration,
            label = "Question duration:",
            value = "${data.questionDuration} s"
        )
    }
}

@Composable
fun DetailRow(iconRes: Int, label: String, value: String, valueColor: Color = Color.Black) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(painter = painterResource(iconRes), contentDescription = null)
            Text(text = label, style = Typography.labelMedium)
        }
        Text(text = value, style = Typography.labelMedium, color = valueColor)
    }
}

