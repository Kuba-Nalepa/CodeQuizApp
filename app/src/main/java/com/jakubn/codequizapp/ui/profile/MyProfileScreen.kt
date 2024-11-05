package com.jakubn.codequizapp.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.ui.home.Header

@Composable
fun MyProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111418))
            .padding(16.dp)
    ) {
        Header()
        ProfileSection("Me")
        MyStatsSection()
        StartGameButton()
    }
}

//@Composable
//fun Header() {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 16.dp),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        Icon(
//            painter = painterResource(id = R.drawable.ic_close),
//            contentDescription = "Close",
//            tint = Color.White,
//            modifier = Modifier.size(24.dp)
//        )
//    }
//}

@Composable
fun ProfileSection(userNickname: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter("https://cdn.usegalileo.ai/stability/1d6be1a0-d882-4f6c-a561-715453e40f07.png"),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(128.dp) // Set the size of the image
                .clip(CircleShape) // Crop the image to be circular
                .border(1.dp, Color.White, CircleShape) // Add a white border
        )

        Text(
            text = userNickname,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_rank),
                    contentDescription = "Rank",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp)
                )
                Text(text = "Rank 1", color = Color.White)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    // TODO  if dynamically chaning icon (due to ratio)
                    painter = painterResource(id = R.drawable.ic_ratio_up),
                    contentDescription = "Win ratio 100%",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp)
                )
                Text(text = "Win ratio 100%", color = Color.White)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_games_played),
                    contentDescription = "Win ratio 100%",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp)
                )
                Text(text = "Games played 100", color = Color.White)
            }
        }
    }
}


@Composable
fun MyStatsSection() {
    Column(
        modifier = Modifier
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Statistics",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Top Row with two boxes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // First Box - Games Played
                MyStatsBox(
                    statsNumber = "50",
                    statsDescription = "Games played",
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                )
                // Second Box - Wins
                MyStatsBox(
                    statsNumber = "25",
                    statsDescription = "Wins",
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                )
            }
            // Bottom Row with one box
            MyStatsBox(
                statsNumber = "25",
                statsDescription = "Losses",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun MyStatsBox(statsNumber: String, statsDescription: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .border(BorderStroke(1.dp, Color.White), RoundedCornerShape(4.dp))
            .padding(8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Text(text = statsNumber, fontSize = 24.sp, color = Color.White)
            Text(text = statsDescription, fontSize = 12.sp, color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMyProfileScreen() {
    MyProfileScreen()
}

@Preview(showBackground = true)
@Composable
fun MyStatsBoxPreview() {
    MyStatsBox(statsNumber = "1preview", statsDescription = "preview")
}

