package com.jakubn.codequizapp.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jakubn.codequizapp.ui.home.Header

@Composable
fun UserProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111418))
            .padding(16.dp)
    ) {
        Header()
        ProfileSection("User")
        ActionButtons()
        StatsSection()
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
fun ActionButtons() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = { /* TODO: Add friend action */ },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF293038))
        ) {
            Text("Add friend")
        }
        Button(
            onClick = { /* TODO: Challenge action */ },
            modifier = Modifier.weight(1f)
        ) {
            Text("Challenge")
        }
    }
}

@Composable
fun StatsSection() {
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
fun StatsBox(statsNumber: String, statsDescription: String, modifier: Modifier = Modifier) {
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

@Composable
fun StartGameButton() {
    Column(
        Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f, false)
        ) {
            //...
        }

        Button(
            onClick = { /* TODO: Start game action */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text("Start Game")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    UserProfileScreen()
}
@Preview(showBackground = true)
@Composable
fun StatsBoxPreview() {
    StatsBox(statsNumber = "1preview", statsDescription = "preview" )
}

