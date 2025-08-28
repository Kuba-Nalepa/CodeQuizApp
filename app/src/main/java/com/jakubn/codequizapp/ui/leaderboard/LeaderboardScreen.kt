package com.jakubn.codequizapp.ui.leaderboard

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.model.Friend
import com.jakubn.codequizapp.model.FriendshipRequest
import com.jakubn.codequizapp.model.User
import com.jakubn.codequizapp.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    user: User,
    navigateToChat: (Friend) -> Unit,
    viewModel: LeaderboardViewModel = hiltViewModel()
) {
    user.uid?.let { viewModel.fetchMyFriends(it) }
    val leaderboardState by viewModel.leaderboardUsers.collectAsState()
    val friendshipStatus by viewModel.friendshipStatus.collectAsState()
    val friendsListState by viewModel.friendsList.collectAsState()

    val sheetState = rememberModalBottomSheetState()
    var selectedUser by remember { mutableStateOf<User?>(null) }
    val isFriend = selectedUser?.uid?.let { uid ->
        friendsListState.any { it.uid == uid }
    } ?: false
    val context = LocalContext.current
    val isSheetVisible = selectedUser != null

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                "friend_request_sent" -> {
                    selectedUser?.let {
                        Toast.makeText(
                            context,
                            "Friend request sent to ${it.name}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                // "player_challenged" -> {     //TODO Later
                //     Toast.makeText(context, "Player challenged!", Toast.LENGTH_SHORT).show()
                // }

            }
        }
    }

    LaunchedEffect(key1 = selectedUser) {
        selectedUser?.let { selected ->
            val myUserId = user.uid ?: return@let
            val otherUserId = selected.uid ?: return@let
            viewModel.startListeningForFriendshipStatus(myUserId, otherUserId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painterResource(R.drawable.background_auth),
                contentScale = ContentScale.FillBounds
            )
            .then(
                if (isSheetVisible) Modifier
                    .background(Color.Black.copy(alpha = 0.2f))
                    .blur(12.dp) else Modifier
            )
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.padding(bottom = 24.dp, top = 8.dp),
            painter = painterResource(R.drawable.icon_login),
            contentDescription = "Logo"
        )

        Text(
            text = "Leaderboard",
            style = Typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (selectedUser != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    selectedUser = null
                },
                sheetState = sheetState,
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
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {
                    selectedUser?.let { selectedUser ->
                        selectedUser.uid?.let { selectedUserUid ->
                            user.uid?.let { userUid ->
                                UserMenuBottomSheetContent(
                                    user = selectedUser,
                                    isFriend = isFriend,
                                    friendshipStatus = friendshipStatus,
                                    inviteFriend = {
                                        viewModel.sendFriendshipRequest(
                                            userUid,
                                            selectedUserUid
                                        )
                                    },
                                    textMessage = {
                                        selectedUser.let { friend ->
                                            navigateToChat(
                                                Friend(
                                                uid = friend.uid,
                                                name = friend.name,
                                                imageUri = friend.imageUri
                                            )
                                            )
                                        }
                                    },
                                    challengePlayer = { /* TODO */ }
                                )
                            }
                        }

                    }
                }
            }
        }

        when (val state = leaderboardState) {
            is CustomState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(48.dp)
                )
            }

            is CustomState.Success -> {
                if (state.result.isEmpty()) {
                    Text(
                        text = "No leaderboard data available.",
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(state.result) { index, user ->
                            LeaderboardItem(
                                position = index + 1,
                                user = user,
                                onCLick = {
                                    selectedUser = user
                                }
                            )
                        }
                    }
                }
            }

            is CustomState.Failure -> {
                Text(
                    text = "Failed to load leaderboard: ${state.message}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            CustomState.Idle -> {}
        }
    }
}

@Composable
private fun LeaderboardItem(position: Int, user: User, onCLick: () -> Unit) {
    val goldColor = Color(0xFFFFD700)
    val silverColor = Color(0xFFC0C0C0)
    val bronzeColor = Color(0xFFCD7F32)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable { onCLick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "$position.",
                    style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.width(36.dp),
                    textAlign = TextAlign.Start
                )
                when (position) {
                    1 -> Icon(
                        painter = painterResource(R.drawable.ic_first_place),
                        contentDescription = "First place",
                        tint = goldColor,
                        modifier = Modifier.size(28.dp)
                    )

                    2 -> Icon(
                        painter = painterResource(R.drawable.ic_second_place),
                        contentDescription = "Second place",
                        tint = silverColor,
                        modifier = Modifier.size(28.dp)
                    )

                    3 -> Icon(
                        painter = painterResource(R.drawable.ic_third_place),
                        contentDescription = "Third place",
                        tint = bronzeColor,
                        modifier = Modifier.size(28.dp)
                    )

                    else -> Spacer(modifier = Modifier.size(28.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary),
                    painter = if (!user.imageUri.isNullOrEmpty()) {
                        rememberAsyncImagePainter(model = Uri.parse(user.imageUri))
                    } else {
                        painterResource(R.drawable.sample_avatar)
                    },
                    contentDescription = "User Avatar"
                )
                user.name?.let { name ->
                    Text(
                        text = name,
                        style = Typography.titleSmall,
                        color = Color.Black,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                } ?: Text(
                    text = "Unknown User",
                    style = Typography.titleSmall,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }
            Text(
                text = "${user.score}",
                style = Typography.titleSmall,
                color = Color.Black,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
private fun UserMenuBottomSheetContent(
    user: User,
    isFriend: Boolean,
    friendshipStatus: CustomState<FriendshipRequest?>,
    inviteFriend: () -> Unit,
    textMessage: () -> Unit,
    challengePlayer: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            painter = rememberAsyncImagePainter(Uri.parse(user.imageUri)),
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )
        user.name?.let { username ->
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = username,
                style = Typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                if (isFriend) {
                    Icon(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(5.dp),
                        painter = painterResource(R.drawable.ic_friend),
                        tint = Color(0xFF007211),
                        contentDescription = "Friend"
                    )
                    Text(style = Typography.labelSmall, text = "Friend")
                } else {
                    when (friendshipStatus) {
                        is CustomState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(40.dp),
                                strokeWidth = 2.dp
                            )
                            Text(style = Typography.labelSmall, text = "Checking...")
                        }

                        is CustomState.Success -> {
                            val request = friendshipStatus.result
                            val buttonText = when (request?.status) {
                                "pending" -> "Sent"
                                else -> "Add"
                            }
                            val isEnabled = request?.status != "pending"
                            Icon(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(100))
                                    .clickable(enabled = isEnabled) { inviteFriend() }
                                    .padding(5.dp),
                                painter = when (request?.status) {
                                    "pending" -> painterResource(R.drawable.ic_avatar)
                                    else -> painterResource(R.drawable.ic_person_add)
                                },
                                tint = when (request?.status) {
                                    "pending" -> Color(0xff58959A)
                                    else -> Color.White
                                },
                                contentDescription = buttonText
                            )
                            Text(
                                style = Typography.labelSmall,
                                text = buttonText,
                            )
                        }

                        is CustomState.Failure -> {
                            Icon(
                                painter = painterResource(R.drawable.ic_error),
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = "Error",
                                style = Typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        CustomState.Idle -> {
                            Icon(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(100))
                                    .clickable { inviteFriend() }
                                    .padding(5.dp),
                                painter = painterResource(R.drawable.ic_person_add),
                                tint = Color.White,
                                contentDescription = "Add friend"
                            )
                            Text(style = Typography.labelSmall, text = "Add friend")
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(100))
                        .clickable { textMessage() }
                        .padding(5.dp),
                    painter = painterResource(R.drawable.ic_message),
                    tint = Color.White,
                    contentDescription = "Message"
                )
                Text(
                    style = Typography.labelSmall,
                    text = "Message",
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(100))
                        .clickable { challengePlayer() }
                        .padding(5.dp),
                    painter = painterResource(R.drawable.ic_play),
                    tint = Color.White,
                    contentDescription = "Challenge the player"
                )
                Text(
                    style = Typography.labelSmall,
                    text = "Challenge"
                )
            }
        }
    }
}