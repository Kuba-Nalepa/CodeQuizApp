package com.jakubn.codequizapp.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.domain.model.Answers
import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.domain.model.Question
import com.jakubn.codequizapp.navigation.Screen
import com.jakubn.codequizapp.theme.Typography
import com.jakubn.codequizapp.ui.game.availableGames.QuizViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.full.memberProperties

@Composable
fun QuizScreen(
    navController: NavController,
    gameId: String,
    viewModel: QuizViewModel = hiltViewModel()
) {
    viewModel.getGameData(gameId)
    val gameState by viewModel.state.collectAsState()
    val isGameFinished by viewModel.isGameFinished.collectAsState()
    val isCounterFinished by viewModel.isCounterFinished.collectAsState()
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    val selectedAnswers = remember { arrayListOf<Int>() }


    LaunchedEffect(isGameFinished) {
        if (isGameFinished) {
            val selectedAnswersJson = Json.encodeToString(selectedAnswers)
            navController.navigate(Screen.GameOver.route + "/$gameId" + "/$selectedAnswersJson")
        }
    }


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

        if (!isCounterFinished) {
            Counter(viewModel, onTimerFinished = { viewModel.setCounterFinished() })
        } else {
            when (val currentGameState = gameState) {
                is CustomState.Success -> {
                    val question = currentGameState.result?.questions?.get(currentQuestionIndex)

                    if (question != null) {
                        currentGameState.result.questionDuration?.let {
                            QuestionTemplate(
                                question = question,
                                selectedOption = selectedOption,
                                onOptionSelected = { index ->
                                    selectedOption = index
                                },
                                onClick = {
                                    selectedOption?.let { selectedAnswers.add(it) }
                                    selectedOption = null

                                    if (currentQuestionIndex == currentGameState.result.questions?.lastIndex) {
                                        viewModel.setGameFinished()
                                    } else {
                                        currentQuestionIndex++
                                        viewModel.resetTimer()
                                    }
                                },
                                duration = it,
                                onTimeFinished = {
                                    if (currentQuestionIndex == currentGameState.result.questions?.lastIndex) {
                                        viewModel.setGameFinished()
                                    } else {
                                        currentQuestionIndex++
                                        viewModel.resetTimer()
                                    }
                                }
                            )
                        }
                    }
                }

                is CustomState.Failure -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(currentGameState.message.toString(), style = Typography.titleLarge)
                    }
                }

                CustomState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }

                CustomState.Idle -> {}
            }
        }
    }
}

@Composable
fun Counter(viewModel: QuizViewModel, onTimerFinished: () -> Unit) {
    val count by viewModel.countDownValue.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startCountdown(onTimerFinished)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 54.dp),
            text = "Quiz starts in...",
            textAlign = TextAlign.Center,
            style = Typography.titleLarge,
        )
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = count.toString(),
            style = Typography.titleLarge,
            fontSize = 88.sp
        )
    }
}

@Composable
fun QuestionTemplate(
    question: Question,
    selectedOption: Int?,
    onOptionSelected: (Int) -> Unit,
    onClick: () -> Unit,
    duration: Int,
    viewModel: QuizViewModel = hiltViewModel(),
    onTimeFinished: () -> Unit,
) {
    val timerProgress = viewModel.timerProgress.collectAsState()
    val isTimeUp by viewModel.isTimeUp.collectAsState()

    LaunchedEffect(question) {
        viewModel.startTimer(duration)
    }

    if (isTimeUp) {
        onTimeFinished()
    }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {
        LinearProgressIndicator(
            progress = { timerProgress.value },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        )

        question.title?.let {
            Text(
                text = it,
                textAlign = TextAlign.Center,
                style = Typography.titleMedium,
                color = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0x80FFFFFF))
                .padding(24.dp)
        ) {
            for ((index, prop) in Answers::class.memberProperties.withIndex()) {
                val isSelected = index == selectedOption
                val answer = question.answers?.let { prop.get(it) }

                if (answer != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                color = if (isSelected)
                                    Color(0x8000FF00)
                                else Color(0x40FFFFFF)
                            )
                            .clickable { onOptionSelected(index) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = null // Click handled by parent
                        )

                        Text(
                            text = answer.toString(),
                            color = Color.Black,
                            style = Typography.labelSmall
                        )
                    }
                }
            }
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            enabled = selectedOption != null,
            onClick = onClick
        ) { Text("Submit") }
    }
}

@Composable
fun CodeQuizText() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(
            modifier = Modifier
                .padding(top = 20.dp),
            text = "<CODE/QUIZ>",
            style = Typography.bodySmall,
            color = Color(0xff7BAFC4)
        )
    }
}