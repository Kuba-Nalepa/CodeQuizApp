package com.jakubn.codequizapp.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.data.User

@Composable
fun HomeScreen() {
    // Root container
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111418))
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top header
        Header()

        // Main content
        MainContent()

        // Leaderboard
        Leaderboard()

        // Bottom navigation
        BottomNavigationBar()
    }
}

@Composable
fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "CodeQuiz",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        IconButton(onClick = { /* Do something */ }) {
            Icon(
                painter = rememberAsyncImagePainter(""),
                contentDescription = "Settings",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun MainContent() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome section
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter("https://cdn.usegalileo.ai/stability/ff363529-0cfd-45e8-9080-9c8f8a887dde.png"),
                contentDescription = "Welcome Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Column {
                Text(
                    text = "Welcome!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Ready to test your knowledge of programming?",
                    fontSize = 16.sp,
                    color = Color(0xFF9DABB8)
                )
                Text(
                    text = "3,000+ questions",
                    fontSize = 16.sp,
                    color = Color(0xFF9DABB8)
                )
            }
        }

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { /* Do something */ },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF293038)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Play Now")
            }
            Button(
                onClick = { /* Do something */ },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Create Game")
            }
        }
    }
}

@Composable
fun Leaderboard() {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text(
            text = "Leaderboard",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        val users = listOf(
            User("John", "https://cdn.usegalileo.ai/stability/60f4b3b8-7688-49a5-82ea-735184c28dd8.png", 4200),
            User("Sarah", "https://cdn.usegalileo.ai/stability/1d6cec9a-fee8-4d1a-b9a6-9859cf3726ba.png", 3400),
            User("Alex", "https://cdn.usegalileo.ai/stability/60536142-430f-4bf0-a5e2-774f55eafaa2.png", 3100)
        )
        users.forEach { user ->
            LeaderboardItem(user)
        }
    }
}

@Composable
fun LeaderboardItem(user: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(user.imageUrl),
            contentDescription = user.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
        )
        Column {
            Text(
                text = "${user.name}",
                fontSize = 16.sp,
                color = Color.White
            )
            Text(
                text = "Score: ${user.score}",
                fontSize = 14.sp,
                color = Color(0xFF9DABB8)
            )
        }
    }
}

@Composable
fun BottomNavigationBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1C2126))
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        BottomNavigationItem(
            icon = painterResource(id = R.drawable.ic_home),
            label = "Home",
            selected = true
        )
        BottomNavigationItem(
            icon = painterResource(id = R.drawable.ic_leaderboard),
            label = "Leaderboards",
            selected = false
        )
        BottomNavigationItem(
            icon = painterResource(id = R.drawable.ic_settings),
            label = "Settings",
            selected = false
        )
    }
}

@Composable
fun BottomNavigationItem(icon: Painter, label: String, selected: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = icon,
            contentDescription = label,
            tint = if (selected) Color.White else Color(0xFF9DABB8),
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) Color.White else Color(0xFF9DABB8)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CodeQuizAppPreview() {
    HomeScreen()
}

