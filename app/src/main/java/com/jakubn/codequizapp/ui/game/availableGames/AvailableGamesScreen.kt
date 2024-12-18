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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.domain.model.Game
import com.jakubn.codequizapp.domain.model.User
import com.jakubn.codequizapp.navigation.Screen
import com.jakubn.codequizapp.theme.CodeQuizAppTheme
import com.jakubn.codequizapp.theme.Typography

@Composable
fun AvailableGameListScreen(
    navController: NavController,
    viewModel: AvailableGamesViewModel = hiltViewModel()
) {
    val gamesState by viewModel.state.collectAsState()

    LaunchedEffect(gamesState) {
        when (gamesState) {
            is CustomState.Success -> {

            }

            is CustomState.Failure -> {

            }

            CustomState.Loading -> {
            }

            CustomState.Idle -> {
                viewModel.getGamesList()
            }
        }
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
                    currentGamesState.result,
                    errorMessage = null,
                    onClick = { gameId, user ->

                        viewModel.addUserToLobby(gameId, user)
                        navController.navigate(route = Screen.Lobby.route + "/$gameId")

                    })

            }

            is CustomState.Failure -> {
                SetGamesList(null, errorMessage = currentGamesState.message, onClick = null)

            }

            CustomState.Loading -> {
                SetGamesList(null, isLoading = true, errorMessage = null, onClick = null)

            }

            CustomState.Idle -> {
                SetGamesList(null, errorMessage = null, onClick = null)

            }
        }
    }
}

@Composable
fun SetGamesList(
    games: List<Game>?,
    isLoading: Boolean = false,
    errorMessage: String?,
    onClick: ((gameId: String, user: User) -> Unit)?
) {
    when {
        isLoading -> {
            // Show a loading indicator when loading
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }

        !games.isNullOrEmpty() -> {
            // Display the list of games
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp), // Add spacing between items
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(games) { game ->
                    GamesListItem(
                        data = game,
                        onClick = {
                            game.gameId?.let { gameId ->
                                game.lobby?.founder?.let { founder ->
                                    onClick?.invoke(gameId, founder)
                                }
                            }
                        }
                    )
                }
            }
        }
        else -> {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                errorMessage?.let {
                    Text(text = it, color = Color.White)
                }
            }
        }
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
            .clickable(true, onClick = onClick)
            .background(Color(0x80FFFFFF))
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                            .shadow(5.dp, shape = CircleShape)
                            .border(1.dp, Color.Black, CircleShape),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(data.lobby?.founder?.imageUri ?: R.drawable.sample_avatar)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(R.drawable.sample_avatar),
                        contentDescription = stringResource(R.string.app_name),
                        contentScale = ContentScale.Crop
                    )
                    data.lobby?.founder?.name?.let { Text(text = it) }
                }

                data.category?.let {
                    Text(
                        modifier = Modifier
                            .border(1.dp, Color(0xFF006134), RoundedCornerShape(5.dp))
                            .clip(RoundedCornerShape(5.dp))
                            .background(color = MaterialTheme.colorScheme.primary)
                            .padding(4.dp),
                        text = it, style = Typography.labelMedium
                    )
                }
            }


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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_players),
                            contentDescription = "Question quantity"
                        )
                        Text(style = Typography.labelMedium, text = "players:")

                    }

                    Text(
                        style = Typography.labelMedium,
                        color = if (data.lobby?.member == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        text = if (data.lobby?.member == null) "1/2" else "2/2"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        Icon(
                            painter = painterResource(R.drawable.ic_quantity),
                            contentDescription = "Question quantity"
                        )
                        Text(style = Typography.labelMedium, text = "question quantity:")


                    }
                    Text(
                        style = Typography.labelMedium,
                        color = Color.Black,
                        text = "5"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        Icon(
                            painter = painterResource(R.drawable.ic_duration),
                            contentDescription = "Question duration"
                        )
                        Text(style = Typography.labelMedium, text = "question duration:")


                    }
                    Text(
                        style = Typography.labelMedium,
                        color = Color.Black,
                        text = "40 s"
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun Xd() {
    CodeQuizAppTheme {
        GamesListItem(Game("1398318417", "Wordpress", null, null, 15), onClick = {})
    }
}