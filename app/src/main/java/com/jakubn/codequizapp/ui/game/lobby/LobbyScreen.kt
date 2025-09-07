package com.jakubn.codequizapp.ui.game.lobby


import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
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
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.model.Friend
import com.jakubn.codequizapp.model.User
import com.jakubn.codequizapp.navigation.Screen
import com.jakubn.codequizapp.theme.Typography

@Composable
fun LobbyScreen(
    user: User,
    navController: NavController,
    gameId: String,
    viewModel: LobbyViewModel = hiltViewModel()
) {
    val gameState by viewModel.state.collectAsState()
    val lobbyState by viewModel.lobby.collectAsState()
    val friendsState by viewModel.friendsState.collectAsState()
    var showFriendsBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(gameState) {
        when (val currentState = gameState) {
            is CustomState.Success -> {
                if (currentState.result == null) navController.popBackStack()
                else if (currentState.result.gameInProgress == false && currentState.result.lobby?.hasFounderLeftGame == true) {
                    viewModel.leaveFromLobby(gameId, user)
                    if (viewModel.isCurrentUserMember(user)) Toast.makeText(
                        context,
                        "Founder has left.\nRemoved from the lobby.",
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.popBackStack()
                } else if (currentState.result.gameInProgress == true) {
                    navController.navigate(Screen.Quiz.route + "/$gameId")
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

    DisposableEffect(gameState) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentGameState = (gameState as? CustomState.Success)?.result
                if (viewModel.isCurrentUserFounder(user)) viewModel.setUserLeftGame(
                    currentGameState,
                    user
                )
                else if (viewModel.isCurrentUserMember(user)) {
                    if (viewModel.isMemberReady() == true) viewModel.changeUserReadinessStatus(
                        gameId,
                        user
                    )
                    viewModel.leaveFromLobby(gameId, user)
                    navController.popBackStack()
                }
            }
        }

        (context as? ComponentActivity)?.onBackPressedDispatcher?.addCallback(callback)

        onDispose {
            callback.remove()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painterResource(R.drawable.background_auth),
                contentScale = ContentScale.FillBounds
            ),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier,
            painter = painterResource(R.drawable.icon_login),
            contentDescription = "Logo"
        )

        when (val currentLobbyState = lobbyState) {
            is CustomState.Success -> {
                currentLobbyState.result?.let { lobby ->

                    PlayerContainer(
                        user = lobby.founder,
                        isReady = lobby.isFounderReady,
                        isFounder = true,
                        currentUserStatus = viewModel.isCurrentUserFounder(user),
                        changeStatus = {
                            gameId.let {
                                viewModel.changeUserReadinessStatus(
                                    it,
                                    user
                                )
                            }
                        }
                    )

                    VersusText(
                        isFounder = viewModel.isCurrentUserFounder(user),
                        isFounderReady = currentLobbyState.result.isFounderReady,
                        isMemberReady = currentLobbyState.result.isMemberReady,
                        playGame = {

                            gameId.let {
                                viewModel.startGame(it)
                                navController.navigate(Screen.Quiz.route + "/$it")

                            }
                        }

                    )

                    if (lobby.member == null) {
                        PlayerContainer(
                            user = null,
                            isLoading = true,
                            inviteFriend = {
                                showFriendsBottomSheet = true
                            }
                        )
                    } else {
                        PlayerContainer(
                            user = lobby.member,
                            isReady = lobby.isMemberReady,
                            isMember = true,
                            currentUserStatus = viewModel.isCurrentUserMember(user),
                            changeStatus = {
                                viewModel.changeUserReadinessStatus(
                                    gameId,
                                    user
                                )
                            }
                        )
                    }
                } ?: viewModel.leaveFromLobby(gameId, user)
            }

            is CustomState.Failure -> {
                PlayerContainer(null, false, currentLobbyState.message)

                VersusText()

                PlayerContainer(null, false, currentLobbyState.message)

            }

            CustomState.Loading -> {
                PlayerContainer(null, true)

                VersusText()

                PlayerContainer(null, true)

            }

            CustomState.Idle -> {
                PlayerContainer(null)

                VersusText()

                PlayerContainer(null)

            }
        }

        if (showFriendsBottomSheet) {
            // Wywołaj pobieranie danych, gdy arkusz zostanie otwarty
            LaunchedEffect(Unit) {
                user.uid?.let { viewModel.getFriends(it) }
            }

            FriendsBottomSheet(
                friendsState = friendsState, // Przekaż CustomState
                onCloseBottomSheet = { showFriendsBottomSheet = false },
                onInviteFriend = { friend ->
                    friend.uid?.let { viewModel.inviteFriend(gameId, it) }
                    showFriendsBottomSheet = false
                }
            )
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
    changeStatus: () -> Unit = {},
    inviteFriend: () -> Unit = {}
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(125.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularProgressIndicator()
                    Button(onClick = inviteFriend) {
                        Text("Invite Friend", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            } else if (user != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .size(80.dp)
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
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = user.name ?: "",
                            style = Typography.bodyLarge,
                            softWrap = true
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_rank),
                                contentDescription = "Rank",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = " ${user.wins}", //TODO user's rank
                                color = MaterialTheme.colorScheme.primary,
                                style = Typography.titleSmall,
                                softWrap = true
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_ranking_up),
                                contentDescription = "Win ratio"
                            )
                            Text(
                                text = " ${user.winRatio}",
                                style = Typography.titleSmall,
                                softWrap = true
                            )
                        }

                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            enabled = (isFounder && currentUserStatus) || (isMember && currentUserStatus),
                            onClick = changeStatus
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ready",
                                    style = Typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                ReadyStatusIcon(isReady = isReady)

                            }
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
fun ReadyStatusIcon(isReady: Boolean) {
    Icon(
        modifier = Modifier.size(24.dp),
        painter = painterResource(
            id = when (isReady) {
                true -> R.drawable.ready_checked
                false -> R.drawable.ready_unchecked
            }
        ),
        contentDescription = when (isReady) {
            true -> "Ready"
            false -> "Not Ready"
        },
        tint = MaterialTheme.colorScheme.onPrimary
    )
}

@Composable
fun VersusText(
    text: String = "VS",
    isFounder: Boolean = false,
    isFounderReady: Boolean = false,
    isMemberReady: Boolean = false,
    playGame: (() -> Unit)? = null
) {
    if (isFounder && (isFounderReady && isMemberReady)) {
        if (playGame != null) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp), onClick = playGame
            ) {
                Text(text = "PLAY", style = Typography.titleMedium)
            }
        }
    } else {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            style = Typography.titleLarge,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsBottomSheet(
    friendsState: CustomState<List<Friend>>,
    onCloseBottomSheet: () -> Unit,
    onInviteFriend: (Friend) -> Unit
) {
    var selectedFriend by remember { mutableStateOf<Friend?>(null) }
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = { onCloseBottomSheet() },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {},
    ) {

        var sheetHeightPx by remember { mutableFloatStateOf(0f) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { coordinates ->
                    sheetHeightPx = coordinates.size.height.toFloat()
                }
                .background(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.32f to Color(0xff000226),
                            0.91f to Color(0xff58959A),
                            1f to Color(0xffffffff)
                        )
                    ),
                    shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
                )
                .padding(horizontal = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Button(
                    onClick = { selectedFriend?.let { onInviteFriend(it) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp),
                    enabled = selectedFriend != null
                ) {
                    Text("Invite", color = MaterialTheme.colorScheme.onPrimaryContainer)
                }

                when (friendsState) {
                    is CustomState.Success -> {
                        if (friendsState.result.isNotEmpty()) {
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f, fill = false),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(friendsState.result) { friend ->
                                    FriendItem(
                                        friend = friend,
                                        isSelected = friend == selectedFriend,
                                        onSelected = { selectedFriend = it }
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No friends found.", color = Color.White)
                            }
                        }
                    }

                    is CustomState.Failure -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Error loading friends: ${friendsState.message}",
                                color = Color.Red
                            )
                        }
                    }

                    is CustomState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    CustomState.Idle -> {}
                }
            }

        }
    }
}

@Composable
fun FriendItem(
    friend: Friend,
    isSelected: Boolean,
    onSelected: (Friend) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onSelected(friend) }
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x52D9D9D9))
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(friend.imageUri)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.sample_avatar),
            error = painterResource(R.drawable.sample_avatar),
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
        )
        friend.name?.let {
            Text(
                text = it,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
        RadioButton(
            selected = isSelected,
            onClick = { onSelected(friend) },
        )
    }
}