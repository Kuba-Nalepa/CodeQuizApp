package com.jakubn.codequizapp.ui.inGame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun QuizScreen() {
    // CustomState for selected option
    val selectedOption = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111418))
            .padding(16.dp)
    ) {
        // Top bar with close icon and progress bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
            }
        }

        LinearProgressIndicator(
            progress = { 0.5f },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = Color.Gray
        )

        // Question text
        Text(
            text = "What is the output of the code?",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Code snippet
        Text(
            text = "const a = [1,2,3]; const b = [1,2,3];\nconsole.log(a === b);",
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier
                .background(Color(0xFF1F1F1F))
                .padding(16.dp)
                .fillMaxWidth()
        )

        // Options
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            listOf("true", "false", "null", "undefined").forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    RadioButton(
                        selected = (selectedOption.value == option),
                        onClick = { selectedOption.value = option },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color.White,
                            unselectedColor = Color.Gray
                        )
                    )
                    Text(
                        text = option,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Timer and Submit button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)){
                TimerDisplay(minutes = "00", seconds = "30")
            }
            Button(
                onClick = { /* Submit answer */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Submit Answer")
            }
        }
    }
}

@Composable
fun TimerDisplay(minutes: String, seconds: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        TimerUnitDisplay(timeUnit = minutes)
        TimerUnitDisplay(timeUnit = seconds)
    }
}

@Composable
fun TimerUnitDisplay(timeUnit: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(50.dp)
            .background(Color(0xFF1F1F1F))
            .padding(8.dp)
    ) {
        Text(
            text = timeUnit,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun QuizScreenPreview() {
    QuizScreen()
}
