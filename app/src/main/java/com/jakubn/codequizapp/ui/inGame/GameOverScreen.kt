package com.jakubn.codequizapp.ui.inGame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jakubn.codequizapp.ui.chat.UserAvatar

@Composable
fun GameOverScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111418))
            .padding(16.dp)
    ) {
        // Close Button
        IconButton(onClick = { /* Close the screen */ }) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Results Header
        Text(
            text = "Results",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // User score
        Text(
            text = "You scored 280 points.",
            color = Color.White,
            fontSize = 16.sp
        )

        // Opponent avatars and score
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Opponent avatars
            Row(modifier = Modifier.padding(vertical = 12.dp)) {
                UserAvatar(imageUrl = "https://path/to/avatar1.jpg")
                Spacer(modifier = Modifier.width(8.dp))
                UserAvatar(imageUrl = "https://path/to/avatar2.jpg")
            }
        }
        // Opponent score
        Text(
            text = "Your opponent scored 400 points.",
            color = Color.White,
            fontSize = 16.sp
        )

        // Game status
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 32.dp)
        ) {
            Icon(Icons.Filled.Info, contentDescription = "Info", tint = Color.Gray, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "You lost",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Better luck next time",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }

        // Action Buttons
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { /* Rematch action */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF14CBCC),
                    contentColor = Color.White
                )
            ) {
                Text("Rematch")
            }

            Button(
                onClick = { /* Go Home action */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF293038),
                    contentColor = Color.White
                )
            ) {
                Text("Go Home")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GameOverScreenPreview() {
    GameOverScreen()
}