package com.jakubn.codequizapp.ui.game.quizReviewScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.model.Answers
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.model.Question
import com.jakubn.codequizapp.model.User
import com.jakubn.codequizapp.theme.Typography
import com.jakubn.codequizapp.ui.game.ErrorScreen
import com.jakubn.codequizapp.ui.game.LoadingScreen
import kotlin.reflect.full.memberProperties

@Composable
fun QuizReviewScreen(
    user: User,
    gameId: String,
    viewModel: QuizReviewViewModel = hiltViewModel()
) {

    val gameState by viewModel.state.collectAsState()

    LaunchedEffect(gameId) {
        viewModel.getGameData(gameId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painterResource(R.drawable.background_auth),
                contentScale = ContentScale.FillBounds
            )
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        when (val currentGameState = gameState) {
            is CustomState.Success -> {
                val game = currentGameState.result
                if (game != null) {
                    val questionList = game.questions.orEmpty()
                    val userAnswerList = viewModel.getUserAnswersList(user).orEmpty()

                    if (questionList.isNotEmpty() && userAnswerList.isNotEmpty()) {

                        VerticalListComposable(questionList, userAnswerList, viewModel)
                    } else {
                        Text(text = "No questions or answers found", color = Color.White)
                    }
                } else {
                    Text(text = "Game data is empty", color = Color.White)
                }
            }

            is CustomState.Failure -> {
                ErrorScreen(currentGameState.message ?: "An error occurred")
            }

            CustomState.Loading -> {
                LoadingScreen()
            }

            CustomState.Idle -> {
                Text(text = "Waiting for data...", color = Color.Gray)
            }
        }
    }
}

@Composable
fun VerticalListComposable(
    questionList: List<Question>,
    userAnswers: List<Int>,
    viewModel: QuizReviewViewModel
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {

        itemsIndexed(items = questionList) { index, question ->

            Column(
                modifier = Modifier
                    .fillParentMaxHeight()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Text("${index + 1}.", style = Typography.titleLarge)

                QuestionTemplateReview(
                    question = question,
                    userSelectedIndex = userAnswers[index],
                    correctIndex = viewModel.correctIndex(question)
                )
            }
        }
    }
}

@Composable
fun QuestionTemplateReview(
    question: Question,
    userSelectedIndex: Int,
    correctIndex: Int
) {

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                                    color = when (index) {
                                        correctIndex -> Color(0x8000FF00)
                                        userSelectedIndex -> Color(0x80FF0000)
                                        else -> Color(0x40FFFFFF)
                                    }
                                )
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            RadioButton(
                                selected = index == userSelectedIndex,
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
    }
}