package com.jakubn.codequizapp.ui.leaderboard

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.model.User

@Composable
fun LeaderboardScreen(
    viewModel: LeaderboardViewModel = hiltViewModel()
) {
    val leaderboardState by viewModel.leaderboardUsers.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painterResource(R.drawable.background_auth),
                contentScale = ContentScale.FillBounds
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
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 24.dp)
        )

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
                            LeaderboardItem(position = index + 1, user = user)
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
            CustomState.Idle -> { }
        }
    }
}

@Composable
private fun LeaderboardItem(position: Int, user: User) {
    val goldColor = Color(0xFFFFD700)
    val silverColor = Color(0xFFC0C0C0)
    val bronzeColor = Color(0xFFCD7F32)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(
                alpha = 0.8f
            )
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
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
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

                user.name?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Black,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                } ?: Text(
                    text = "Unknown User",
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }

            Text(
                text = "${user.score}",
                style = MaterialTheme.typography.titleSmall,
                color = Color.Black,
                textAlign = TextAlign.End
            )
        }
    }
}