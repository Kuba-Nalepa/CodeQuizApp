package com.jakubn.codequizapp.ui.notifications

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.model.Notification
import com.jakubn.codequizapp.theme.Typography

@Composable
fun NotificationsScreen(
    notificationsViewModel: NotificationsViewModel = hiltViewModel(),
    userId: String
) {
    LaunchedEffect(Unit) {
        notificationsViewModel.fetchNotifications(userId)
    }

    val notificationsState by notificationsViewModel.notificationsState.collectAsState()

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
            .background(brush = Brush.verticalGradient(colorStops = colors)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "<CODE/QUIZ>",
                style = Typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        when (val state = notificationsState) {
            is CustomState.Success -> {
                val notifications = state.result
                if (notifications.isNotEmpty()) {
                    Text(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        text = "You have ${notifications.size} notifications remaining",
                        style = Typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    LazyColumn(
                        modifier = Modifier.padding(top = 20.dp)
                    ) {
                        items(notifications) { notification ->
                            when (notification) {
                                is Notification.FriendInvite -> FriendNotificationItem(
                                    notification = notification,
                                    onAccept = { friendshipId ->
                                        notificationsViewModel.handleFriendshipAccept(friendshipId)
                                    },
                                    onDecline = { friendshipId ->
                                        notificationsViewModel.handleFriendshipDecline(friendshipId)

                                    }
                                )

                                is Notification.GameInvite -> GameNotificationItem(
                                    notification = notification,
                                    onAccept = { gameId ->
                                        notificationsViewModel.handleGameInvitationAccept(gameId)
                                    },
                                    onDecline = { gameId ->
                                        notificationsViewModel.handleGameInvitationDecline(gameId)

                                    }
                                )
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Icon(
                            painter = painterResource(R.drawable.ic_notifications_off),
                            contentDescription = "No notifications found",
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                        Text(
                            text = "You don't have any notifications.",
                            textAlign = TextAlign.Center,
                            style = Typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                    }

                }
            }

            is CustomState.Failure -> {
                val message = state.message
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Failed to fetch notifications due to: $message",
                        textAlign = TextAlign.Center,
                        style = Typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            is CustomState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            CustomState.Idle -> {
                // Do nothing
            }
        }
    }
}

@Composable
fun FriendNotificationItem(
    notification: Notification.FriendInvite,
    onAccept: (String) -> Unit,
    onDecline: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x52D9D9D9)),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 8.dp, top = 8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            ) {
                var isLoading by remember { mutableStateOf(true) }

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(notification.request.senderImageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "User Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                    onLoading = { isLoading = true },
                    onSuccess = { isLoading = false },
                    onError = { isLoading = false }
                )

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.LightGray.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                buildAnnotatedString {
                    append("Player ")

                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        append(notification.request.senderName)
                    }

                    append(" has invited You to friends.")
                },
                style = Typography.bodyMedium
            )
            Row(modifier = Modifier.padding(8.dp)) {
                Button(onClick = { notification.request.id?.let { onAccept(it) } }) {
                    Text("Accept", color = MaterialTheme.colorScheme.onPrimary)
                }
                Spacer(modifier = Modifier.size(10.dp))
                OutlinedButton(
                    onClick = { notification.request.id?.let { onDecline(it) } },
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Decline")
                }
            }
        }
    }
}

@Composable
fun GameNotificationItem(
    notification: Notification.GameInvite,
    onAccept: (String) -> Unit,
    onDecline: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x52D9D9D9)),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 8.dp, top = 8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            ) {
                var isLoading by remember { mutableStateOf(true) }

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(notification.request.senderImageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "User Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                    onLoading = { isLoading = true },
                    onSuccess = { isLoading = false },
                    onError = { isLoading = false }
                )

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.LightGray.copy(alpha = 0.3f)), // dim overlay
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        append(notification.request.senderName)
                    }

                    append(" has challenged You. ")
                },
                style = Typography.bodyMedium
            )

            Row(modifier = Modifier.padding(8.dp)) {
                Button(onClick = { onAccept(notification.request.gameId) }) {
                    Text("Compete", color = MaterialTheme.colorScheme.onPrimary)
                }

                Spacer(modifier = Modifier.size(10.dp))

                OutlinedButton(
                    onClick = { onDecline(notification.request.gameId) },
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Not now")
                }
            }
        }
    }
}
