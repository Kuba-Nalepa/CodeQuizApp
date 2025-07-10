package com.jakubn.codequizapp.ui.game

import android.widget.Toast
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.model.Answers
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.model.Question
import com.jakubn.codequizapp.model.User
import com.jakubn.codequizapp.navigation.Screen
import com.jakubn.codequizapp.theme.Typography
import kotlinx.coroutines.delay
import kotlin.reflect.full.memberProperties

@Composable
fun QuizScreen(
    user: User,
    navController: NavController,
    gameId: String,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val gameState by viewModel.state.collectAsState()
    val isGameFinished by viewModel.isGameFinished.collectAsState()

    var isCounterActive by remember { mutableStateOf(true) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    val selectedAnswers = remember { arrayListOf<Int>() }

    val gameStateSnapshot by rememberUpdatedState(gameState)

    LaunchedEffect(gameId) {
        viewModel.listenGameData(gameId)
    }

    LaunchedEffect(isGameFinished) {
        if (isGameFinished) {
            when (val currentState = gameStateSnapshot) {
                is CustomState.Success -> {
                    currentState.result?.questions?.let { questions ->
                        val correctAnswers = viewModel.checkAnswers(questions, selectedAnswers)
                        currentState.result.lobby?.let { lobby ->
                            viewModel.saveUserGameStats(gameId, lobby, user, selectedAnswers, correctAnswers)
                            viewModel.setUserFinishedGame(gameId, lobby, user, true)
                        }
                    }
                    navController.navigate(Screen.GameOver.route + "/$gameId")
                }
                is CustomState.Failure -> Toast.makeText(context, currentState.message, Toast.LENGTH_SHORT).show()
                else -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .paint(painterResource(R.drawable.background_auth), contentScale = ContentScale.FillBounds)
            .padding(horizontal = 24.dp)
    ) {
        CodeQuizText()
        if (isCounterActive) {
            Counter(
                onTimerFinished = { isCounterActive = false },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            when (val currentGameState = gameState) {
                is CustomState.Success -> {
                    currentGameState.result?.questions?.getOrNull(currentQuestionIndex)?.let { question ->
                        QuestionTemplate(
                            question = question,
                            selectedOption = selectedOption,
                            onOptionSelected = { selectedOption = it },
                            onClick = {
                                selectedOption?.let { selectedAnswers.add(it) }
                                selectedOption = null

                                if (currentQuestionIndex == currentGameState.result.questions?.lastIndex) {
                                    viewModel.setGameFinished()
                                } else {
                                    currentQuestionIndex++
                                }
                            },
                            duration = currentGameState.result.questionDuration ?: 10,
                            onTimeFinished = {
                                selectedAnswers.add(selectedOption ?: -1)
                                selectedOption = null

                                if (currentQuestionIndex == currentGameState.result.questions?.lastIndex) {
                                    viewModel.setGameFinished()
                                } else {
                                    currentQuestionIndex++
                                }
                            }
                        )
                    }
                }
                is CustomState.Failure -> ErrorScreen(currentGameState.message.toString())
                CustomState.Loading -> LoadingScreen()
                CustomState.Idle -> {}
            }
        }
    }
}

@Composable
fun QuestionTemplate(
    question: Question,
    selectedOption: Int?,
    onOptionSelected: (Int) -> Unit,
    onClick: () -> Unit,
    duration: Int,
    onTimeFinished: () -> Unit
) {

    var timerProgress by remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    var isTimeUp by remember { mutableStateOf(false) }

    LaunchedEffect(question) {
        timerProgress = 0f
        isTimeUp = false

        val tickInterval = 100L
        val totalTicks = duration * 1000 / tickInterval

        for (tick in 0..totalTicks) {
            timerProgress = tick.toFloat() / totalTicks
            delay(tickInterval)
        }

        isTimeUp = true
        onTimeFinished()
    }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {
        LinearProgressIndicator(
            progress = { timerProgress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        )

        Text(
            text = question.title.orEmpty(),
            textAlign = TextAlign.Center,
            style = Typography.titleMedium,
            color = Color.White
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0x80FFFFFF))
                .padding(24.dp)
        ) {
            question.answers?.let { answers ->
                Answers::class.memberProperties.forEachIndexed { index, prop ->
                    val answerText = prop.get(answers)?.toString()
                    if (!answerText.isNullOrBlank()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    color = if (index == selectedOption)
                                        Color(0x8000FF00)
                                    else Color(0x40FFFFFF)
                                )
                                .clickable { onOptionSelected(index) }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            RadioButton(
                                selected = index == selectedOption,
                                onClick = null
                            )

                            Text(
                                text = answerText,
                                color = Color.Black,
                                style = Typography.labelSmall
                            )
                        }
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
fun ErrorScreen(errorMessage: String) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(errorMessage, style = Typography.titleLarge)
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun Counter(
    onTimerFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var count by remember { mutableIntStateOf(5) }

    LaunchedEffect(Unit) {
        while (count > 0) {
            delay(1000L)
            count--
        }
        onTimerFinished()
    }

    Box(modifier = modifier) {
        Text(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 54.dp),
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
fun CodeQuizText() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(
            modifier = Modifier
                .padding(top = 22.dp),
            text = "<CODE/QUIZ>",
            style = Typography.bodySmall,
            color = Color(0xff7BAFC4)
        )
    }
}