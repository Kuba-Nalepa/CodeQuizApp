package com.jakubn.codequizapp.ui.game.createGame

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonColors
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.theme.Typography

@Composable
fun CreateGameScreen(navController: NavHostController,viewModel: CreateGameViewModel = hiltViewModel()) {
    val createGameState by viewModel.state.collectAsState()
    val context = LocalContext.current
    var quizCategorySelected by remember { mutableStateOf("") }
    var indexSelection by remember { mutableIntStateOf(0) }
    val quizNumberSelected by remember {
        derivedStateOf {
            when (indexSelection) {
                0 -> 5
                1 -> 10
                else -> 15
            }
        }
    }
    var quizTimeSecondsIndicator by remember { mutableIntStateOf(15) }
    val contentList = hashMapOf(
        "Linux" to R.drawable.linux_image,
        "DevOps" to R.drawable.devops_image,
        "Wordpress" to R.drawable.wordpress_image,
        "Docker" to R.drawable.docker_image,
        "NodeJs" to R.drawable.node_js_image,
        "SQL" to R.drawable.sql_image
    )

    LaunchedEffect(createGameState, context) {
        when (val currentCreateGameState = createGameState) {
            is CustomState.Success -> {
                viewModel.resetState()
//                navController.navigate()
            }

            is CustomState.Failure -> {
                val message = currentCreateGameState.message
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

            }

            CustomState.Idle -> {}
            CustomState.Loading -> {}

        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(modifier = Modifier.padding(vertical = 10.dp), text = "Choose category")

            LazyVerticalGrid(
                modifier = Modifier.fillMaxWidth(),
                columns = GridCells.Adaptive(128.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items = contentList.toList()) { item ->
                    CategorySelection(item.first, item.second, quizCategorySelected == item.first) {
                        quizCategorySelected = it
                    }
                }
            }

            Text(
                modifier = Modifier.padding(vertical = 10.dp),
                text = "Select the number of questions in Quiz"
            )

            QuizNumberSelection(indexSelection) {
                indexSelection = it

            }

            Text(
                modifier = Modifier.padding(vertical = 10.dp),
                text = "Set the amount of time for every question"
            )

            Text("$quizTimeSecondsIndicator seconds")

            QuestionTimeSlider(quizTimeSecondsIndicator) { quizTimeSecondsIndicator = it.toInt() }

        }

        when (createGameState) {
            CustomState.Loading -> {
                Row (
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Bottom))

                }
            }

            else -> {
                Button(
                    modifier = Modifier.fillMaxWidth(), onClick = {
                        viewModel.createGame(
                            quizCategorySelected,
                            quizNumberSelected,
                            quizTimeSecondsIndicator
                        )
                    },
                    enabled = quizCategorySelected.isNotEmpty()
                ) {
                    Text(modifier = Modifier.align(Alignment.Bottom), text = "Create Game")
                }
            }
        }
    }
}

@Composable
fun CategorySelection(text: String, drawable: Int, isActive: Boolean = false, onCLick: (id: String) -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .height(intrinsicSize = IntrinsicSize.Max)
            .height(100.dp)
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(10.dp),
                spotColor = Color.Black
            )
            .paint(
                painter = painterResource(drawable),
                sizeToIntrinsics = false,
                contentScale = ContentScale.FillWidth
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = {
                    onCLick(text)
                })
            .background(
                if (isActive) Color(0x80003D0D) else Color(0x80000000),
                shape = RectangleShape

            )

    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .align(Alignment.Center),
            text = text,
            color = if (isActive) MaterialTheme.colorScheme.primary else Color.White,
            style = Typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun QuizNumberSelection(index: Int, onUpdateIndex: (index: Int) -> Unit) {
    val quantityNumberList = listOf("5 questions", "10 questions", "15 questions")

    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, shape = RoundedCornerShape(25.dp), color = Color.Black)
            .padding(horizontal = 5.dp)
    ) {
        quantityNumberList.forEachIndexed { idx, string ->
            SegmentedButton(colors = SegmentedButtonColors(
                activeContainerColor = Color(0xFFA3FF0D),
                activeContentColor = Color(0xFF74B583),
                activeBorderColor = Color(0xFF003963),
                inactiveContentColor = Color(0xFF58959A),
                inactiveBorderColor = Color(0xFF003963),
                inactiveContainerColor = Color.White,
                disabledInactiveContainerColor = Color(0x00000000),
                disabledActiveBorderColor = Color(0x00000000),
                disabledActiveContentColor = Color(0x00000000),
                disabledInactiveBorderColor = Color(0x00000000),
                disabledActiveContainerColor = Color(0x00000000),
                disabledInactiveContentColor = Color(0x00000000)
            ),
                border = SegmentedButtonDefaults.borderStroke(Color.White, 0.dp),
                icon = { SegmentedButtonDefaults.Icon(false) },
                shape = RoundedCornerShape(20.dp),
                selected = idx == index,
                onClick = {
                    onUpdateIndex(idx)
                }
            ) {
                Text(
                    text = string,
                    style = Typography.labelSmall,
                    color = Color.Black,
                )
            }
        }
    }
}

@Composable
fun QuestionTimeSlider(number: Int, onValueChange: (indicator: Float) -> Unit) {
    Slider(
        value = number.toFloat(),
        onValueChange = { onValueChange(it) },
        valueRange = 15f..60f
    )
}