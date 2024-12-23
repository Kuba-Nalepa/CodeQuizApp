package com.jakubn.codequizapp.ui.game.lobby

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.domain.model.User
import com.jakubn.codequizapp.theme.Typography

@Composable
fun LobbyScreen(
    user: User,
    navController: NavController,
    gameId: String? = null,
    viewModel: LobbyViewModel = hiltViewModel()
) {
    val lobbyState by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(lobbyState) {
        when (val currentState = lobbyState) {
            is CustomState.Success -> if(currentState.result == null)  navController.popBackStack()
            is CustomState.Failure -> Toast.makeText(context, currentState.message, Toast.LENGTH_SHORT).show()
            CustomState.Idle -> gameId?.let { viewModel.getLobbyData(it) }
            CustomState.Loading -> {}
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            gameId?.let {
                viewModel.removeFromLobby(gameId, user)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .paint(painterResource(R.drawable.background_auth), contentScale = ContentScale.FillBounds),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .fillMaxHeight(0.2f)
                .zIndex(1f)
                .padding(top = 20.dp),
            painter = painterResource(R.drawable.icon_login),
            contentDescription = "Logo"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(2f),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            when (val currentLobbyState = lobbyState) {
                is CustomState.Success -> {
                    currentLobbyState.result?.let { lobby ->

                        PlayerContainer(
                            user = lobby.founder,
                            isReady = lobby.isFounderReady,
                            isFounder = true,
                            currentUserStatus =  viewModel.isCurrentUserFounder(user),
                            changeStatus = { gameId?.let { viewModel.changeUserReadinessStatus(it, user) } }
                        )

                        VersusText("VS")

                        if (lobby.member == null) {
                            PlayerContainer(
                                user = null,
                                isLoading = true,
                            )
                        } else {
                            PlayerContainer(
                                user = lobby.member,
                                isReady = lobby.isMemberReady,
                                isMember = true,
                                currentUserStatus =  viewModel.isCurrentUserMember(user),
                                changeStatus = { gameId?.let { viewModel.changeUserReadinessStatus(it, user) } }
                            )
                        }
                    }
                }
                is CustomState.Failure -> {
                    PlayerContainer(null, false, currentLobbyState.message)

                    VersusText("VS")

                    PlayerContainer(null, false, currentLobbyState.message)

                }
                CustomState.Loading -> {
                    PlayerContainer(null, true)

                    VersusText("VS")

                    PlayerContainer(null, true)

                }
                CustomState.Idle -> {
                    PlayerContainer(null)
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "VS",
                        style = Typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                    PlayerContainer(null)

                }
            }
        }
    }
}

@Composable
fun PlayerContainer(
    user: User? = null,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    isReady: Boolean = false,
    isFounder: Boolean = false,
    currentUserStatus: Boolean = false,
    isMember: Boolean = false,
    changeStatus: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .padding(20.dp)
            .background(Color(0xB33495AC), RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .offset(y = (-20).dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xB33495AC))
                .padding(20.dp)
                .fillMaxWidth()
                .height(intrinsicSize = IntrinsicSize.Max)
                .zIndex(1f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (isLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(125.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    CircularProgressIndicator()
                    Text(modifier = Modifier.padding(top = 20.dp), text = "Waiting for the other player", style = Typography.labelLarge, textAlign = TextAlign.Center)
                }
            } else if (user != null) {
                AsyncImage(
                    modifier = Modifier
                        .size(125.dp)
                        .shadow(5.dp, shape = CircleShape)
                        .border(1.dp, Color.Black, CircleShape),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.imageUri ?: R.drawable.sample_avatar)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.sample_avatar),
                    contentDescription = stringResource(R.string.app_name),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = user.name ?: "",
                        style = Typography.bodyLarge,
                        softWrap = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_rank),
                            contentDescription = "Rank",
                            tint = Color(0xFFA3FF0D)
                        )
                        Text(
                            text = " ${user.wins}", //TODO user's rank
                            color = Color(0xFFA3FF0D),
                            style = Typography.labelLarge,
                            softWrap = true
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_ranking_up),
                            contentDescription = "Win ratio"
                        )
                        Text(
                            text = " ${user.winRatio}",
                            style = Typography.labelLarge,
                            softWrap = true
                        )
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = (isFounder && currentUserStatus) || (isMember && currentUserStatus),
                        onClick = changeStatus
                    ) {
                        if (isReady) {
                            Text("Not ready")
                        } else {
                            Text("Ready")
                        }
                    }
                }
            } else if (errorMessage != null) {
                Text(errorMessage)
            }
        }
    }
}

@Composable
fun VersusText(text: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = text,
        style = Typography.titleLarge,
        textAlign = TextAlign.Center
    )
}