package com.jakubn.codequizapp.ui.home

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.model.Friend
import com.jakubn.codequizapp.model.User
import com.jakubn.codequizapp.theme.Typography
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    createGame: () -> Unit,
    playGame: () -> Unit
) {

    val userState by homeViewModel.state.collectAsState()
    val friendsState by homeViewModel.friendsState.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }

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
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "<CODE/QUIZ>",
            style = Typography.bodyMedium,
            color = Color(0xff7BAFC4)
        )

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
                    color = Color(0xffA3FF0D),
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
                    color = Color(0xFF000000),
                    style = Typography.bodyLarge,
                    softWrap = true

                )
            }

            if (showBottomSheet) {
                val sheetState = rememberModalBottomSheetState()
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState
                ) {
                    FriendsListBottomSheet(
                        friendsState = friendsState,
                        onCloseBottomSheet = { showBottomSheet = false }
                    )
                }
            }
        }
        }
    }

@Composable
fun UserSectionContainer(userState: CustomState<User?>, friendsState: CustomState<List<Friend>>, onFriendsClick: () -> Unit) {

    when (userState) {
        is CustomState.Success -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
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
                        color = Color(0xffA3FF0D)
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
                modifier = Modifier.padding(vertical = 20.dp).fillMaxWidth(),
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
                modifier = Modifier
                    .fillMaxWidth(),
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
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }

        CustomState.Idle -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
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
    onCloseBottomSheet: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Friends",
                style = Typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (friendsState) {
            is CustomState.Success -> {
                val friendsList = friendsState.result
                if (friendsList.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(friendsList) { friend ->
                            FriendMenuItem(
                                friend = friend,
                                onClick = {
                                    TODO()
                                }
                            )
                        }
                    }
                } else {
                    Text(
                        text = "You don't have any friends yet.",
                        style = Typography.bodyLarge,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }

            is CustomState.Failure -> {
                Text(
                    text = "Failed to load friends: ${friendsState.message}",
                    style = MaterialTheme.typography.bodyLarge,
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
private fun FriendMenuItem(friend: Friend, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable { onClick() },
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
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary),
                painter = if (!friend.imageUri.isNullOrEmpty()) {
                    rememberAsyncImagePainter(model = Uri.parse(friend.imageUri))
                } else {
                    painterResource(R.drawable.sample_avatar)
                },
                contentDescription = "User Avatar"
            )

            Spacer(modifier = Modifier.size(8.dp))

            friend.name?.let { name ->
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Black,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
            } ?: Text(
                text = "Unknown User",
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
        }
    }
}