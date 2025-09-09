package com.jakubn.codequizapp.ui.home

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.model.Friend
import com.jakubn.codequizapp.model.FriendshipRequest
import com.jakubn.codequizapp.model.User
import com.jakubn.codequizapp.theme.Typography
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    createGame: () -> Unit,
    playGame: () -> Unit,
    navigateToChat: (Friend) -> Unit,
    navigateToNotifications: () -> Unit
) {
    val userState by homeViewModel.state.collectAsState()
    val friendsState by homeViewModel.friendsState.collectAsState()
    val requestsState by homeViewModel.friendshipRequestsState.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val notificationsNumberState by homeViewModel.notificationsNumberState.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Permission is granted
            } else {
                // Permission is denied.
            }
        }
    )

    LaunchedEffect(key1 = true) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val colors = arrayOf(
        0.06f to Color(0xffA3FF0D),
        0.22f to Color(0xff74B583),
        0.39f to Color(0xff58959A),
        0.62f to Color(0xff003963),
        0.95f to Color(0xff000226)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colorStops = colors))
            .then(
                if (showBottomSheet) Modifier
                    .background(Color.Black.copy(alpha = 0.2f))
                    .blur(12.dp) else Modifier
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "<CODE/QUIZ>",
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 20.dp),
            ) {

                NotificationBadge(onClick = { navigateToNotifications() }, notificationsNumberState)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UserSectionContainer(userState, friendsState) { showBottomSheet = true }

            Text(
                modifier = Modifier.padding(bottom = 20.dp),
                text = "Ready to test your programming skills?",
                style = Typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x52FFFFFF)),
                    shape = RoundedCornerShape(20.dp),
                    onClick = createGame
                ) {
                    Text(
                        text = "Create Game".uppercase(),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        style = Typography.bodyLarge,
                        softWrap = true
                    )
                }
                Button(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    onClick = playGame,
                ) {
                    Text(
                        text = "Play Now".uppercase(),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = Typography.bodyLarge,
                        softWrap = true
                    )
                }

                if (showBottomSheet) {
                    val sheetState = rememberModalBottomSheetState()
                    ModalBottomSheet(
                        onDismissRequest = {
                            showBottomSheet = false
                        },
                        sheetState = sheetState,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        dragHandle = {}
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colorStops = arrayOf(
                                            0.32f to Color(0xff000226),
                                            0.91f to Color(0xff58959A),
                                            1f to Color(0xffffffff)
                                        )
                                    ),
                                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                                )
                                .padding(horizontal = 28.dp)
                        ) {
                            FriendsListBottomSheet(
                                friendsState = friendsState,
                                requestsState = requestsState,
                                onCloseBottomSheet = { showBottomSheet = false },
                                onAcceptRequest = { friendshipId ->
                                    homeViewModel.acceptFriendshipRequest(friendshipId)
                                },
                                onDeclineRequest = { friendshipId ->
                                    homeViewModel.declineFriendshipRequest(friendshipId)
                                },
                                navigateToChat = navigateToChat
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserSectionContainer(
    userState: CustomState<User?>,
    friendsState: CustomState<List<Friend>>,
    onFriendsClick: () -> Unit
) {
    when (userState) {
        is CustomState.Success -> {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "hi ${userState.result?.name}".lowercase(Locale.ROOT),
                        style = Typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        maxLines = 2
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "you have already".lowercase(Locale.ROOT),
                        style = Typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 20.dp)
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(Color(0x52D9D9D9)),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${userState.result?.gamesPlayed}",
                    style = Typography.titleMedium,
                    fontSize = 44.sp
                )
                Text(
                    modifier = Modifier.padding(vertical = 20.dp),
                    text = "games\nplayed",
                    style = Typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(shape = RoundedCornerShape(20.dp))
                        .background(Color(0x52D9D9D9)),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
                        text = "${userState.result?.wins}",
                        style = Typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        modifier = Modifier.padding(bottom = 10.dp),
                        text = "wins",
                        style = Typography.titleSmall,
                        textAlign = TextAlign.Center
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(shape = RoundedCornerShape(20.dp))
                        .background(Color(0x52D9D9D9)),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
                        text = "${(userState.result?.gamesPlayed)?.minus((userState.result.wins))}",
                        style = Typography.titleMedium
                    )
                    Text(
                        modifier = Modifier.padding(bottom = 10.dp),
                        text = "losses",
                        style = Typography.titleSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Button(
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .fillMaxWidth(),
                onClick = onFriendsClick
            ) {
                Image(
                    modifier = Modifier,
                    painter = painterResource(R.drawable.ic_players),
                    contentDescription = "Friends icon"
                )
                Text(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    text = "friends",
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    style = Typography.titleMedium
                )
                Spacer(modifier = Modifier.weight(1f))
                when (friendsState) {
                    is CustomState.Success -> {
                        val friendsCount = friendsState.result.size
                        Text(
                            modifier = Modifier,
                            text = friendsCount.toString(),
                            textAlign = TextAlign.Center,
                            color = Color.Black,
                            style = Typography.titleMedium
                        )
                    }

                    is CustomState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.Black
                        )
                    }

                    else -> {
                        Text(
                            modifier = Modifier,
                            text = "-",
                            textAlign = TextAlign.Center,
                            color = Color.Black,
                            style = Typography.titleMedium
                        )
                    }
                }
            }
        }

        is CustomState.Failure -> {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Failed to fetch user's data.",
                    textAlign = TextAlign.Center,
                    style = Typography.bodyLarge
                )
            }
        }

        CustomState.Loading -> {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }

        CustomState.Idle -> {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Nothing
            }
        }
    }
}

@Composable
fun FriendsListBottomSheet(
    friendsState: CustomState<List<Friend>>,
    requestsState: CustomState<List<FriendshipRequest>>,
    onCloseBottomSheet: () -> Unit,
    onAcceptRequest: (String) -> Unit,
    onDeclineRequest: (String) -> Unit,
    navigateToChat: (Friend) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (requestsState) {
            is CustomState.Success -> {
                val invitations = requestsState.result
                if (invitations.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pending Invitations",
                            style = Typography.titleSmall
                        )
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(invitations) { request ->
                            FriendRequestItem(
                                request = request,
                                onAccept = { request.id?.let { onAcceptRequest(it) } },
                                onDecline = { request.id?.let { onDeclineRequest(it) } }
                            )
                        }
                    }
                } else {
                    // Nothing to show
                }
            }

            is CustomState.Failure -> {
                Text(
                    text = "Failed to load invitations: ${requestsState.message}",
                    style = Typography.labelSmall,
                    textAlign = TextAlign.Center
                )
            }

            is CustomState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }

            is CustomState.Idle -> {
                // Do nothing
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Friends",
                style = Typography.titleSmall
            )
        }

        when (friendsState) {
            is CustomState.Success -> {
                val friendsList = friendsState.result
                if (friendsList.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(friendsList) { friend ->
                            FriendMenuItem(
                                friend = friend,
                                textFriend = {
                                    navigateToChat(friend)
                                },
                                playGame = {
                                    TODO()
                                }
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 150.dp),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start,
                            text = "You don't have any friends yet.",
                            style = Typography.bodySmall
                        )

                        Icon(
                            modifier = Modifier
                                .size(80.dp)
                                .align(Alignment.CenterHorizontally),
                            painter = painterResource(R.drawable.ic_person_add),
                            tint = Color.White,
                            contentDescription = "Add some friends"
                        )
                    }

                }
            }

            is CustomState.Failure -> {
                Text(
                    text = "Failed to load friends: ${friendsState.message}",
                    style = Typography.bodyMedium,
                    modifier = Modifier.padding(top = 16.dp),
                    textAlign = TextAlign.Center
                )
            }

            is CustomState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            }

            is CustomState.Idle -> {
                // Nothing
            }
        }
    }
}

@Composable
fun FriendRequestItem(
    request: FriendshipRequest,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x52D9D9D9))
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(request.senderImageUri)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.sample_avatar),
            error = painterResource(R.drawable.sample_avatar),
            contentDescription = "User Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = "${request.senderName}",
            style = Typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Row {
            IconButton(onClick = onAccept) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Accept",
                    tint = Color.White
                )
            }
            IconButton(onClick = onDecline) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Decline",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun FriendMenuItem(friend: Friend, textFriend: () -> Unit, playGame: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxSize()
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
            contentDescription = "User Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.size(16.dp))
        friend.name?.let { name ->
            Text(
                text = name,
                style = Typography.bodyMedium,
                maxLines = 1,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.primary
            )
        } ?: Text(
            text = "Unknown User",
            style = Typography.bodyMedium,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.weight(1f))

        Row {
            IconButton(onClick = textFriend) {
                Icon(
                    painter = painterResource(R.drawable.ic_message),
                    tint = Color.White,
                    contentDescription = "Text a message"
                )
            }

            IconButton(onClick = playGame) {
                Icon(
                    painter = painterResource(R.drawable.ic_play),
                    tint = Color.White,
                    contentDescription = "Play with friend"
                )
            }
        }
    }
}

@Composable
private fun NotificationBadge(onClick: () -> Unit, notificationsNumberState: CustomState<Int>) {
    when (notificationsNumberState) {
        is CustomState.Success -> {
            val notificationsNumber = notificationsNumberState.result
            BadgedBox(
                badge = {
                    if (notificationsNumber > 0) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = Color.White
                        ) {
                            Text("$notificationsNumber")
                        }
                    }
                }
            ) {
                IconButton(
                    onClick = { onClick() },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_notifications),
                        contentDescription = "Notifications bell",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        is CustomState.Failure -> {
            IconButton(onClick = onClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = "Something went wrong",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        CustomState.Loading -> {
            CircularProgressIndicator()
        }

        CustomState.Idle -> {}
    }
}