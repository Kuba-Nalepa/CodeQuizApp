package com.jakubn.codequizapp.ui.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.theme.Typography
import kotlinx.coroutines.delay

@Composable
fun QuizScreen(gameId: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painterResource(R.drawable.background_auth),
                contentScale = ContentScale.FillBounds
            )
            .padding(horizontal = 24.dp)
    ) {

        CodeQuizText()

        Counter()
    }
}

@Composable
fun Counter() {
    var timeLeft by remember { mutableIntStateOf(5) }
    LaunchedEffect(Unit) {
        while (timeLeft > 1) {
            delay(1000L)
            timeLeft -= 1
        }

    }
    Box(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 54.dp),
                text = "Quiz starts in...",
                textAlign = TextAlign.Center,
                style = Typography.titleLarge,
            )
            Text(modifier = Modifier.align(Alignment.Center) ,text = timeLeft.toString(), style = Typography.titleLarge, fontSize = 88.sp)


    }
}

@Composable
fun CodeQuizText() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(
            modifier = Modifier
                .padding(top = 20.dp),
            text = "<CODE/QUIZ>",
            style = Typography.bodyMedium,
            color = Color(0xff7BAFC4)
        )
    }

}
