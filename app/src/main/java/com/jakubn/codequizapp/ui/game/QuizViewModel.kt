package com.jakubn.codequizapp.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.data.repositoryImpl.GameRepository
import com.jakubn.codequizapp.model.CorrectAnswers
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.model.Game
import com.jakubn.codequizapp.model.Lobby
import com.jakubn.codequizapp.model.Question
import com.jakubn.codequizapp.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.full.memberProperties

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _state = MutableStateFlow<CustomState<Game?>>(CustomState.Idle)
    val state: StateFlow<CustomState<Game?>> = _state

    private val _isGameFinished = MutableStateFlow(false)
    val isGameFinished: StateFlow<Boolean> = _isGameFinished

    fun listenGameData(gameId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            gameRepository.listenGameDataChanges(gameId)
                .onStart {
                    _state.value = CustomState.Loading
                }
                .catch { throwable ->
                    _state.value = CustomState.Failure(throwable.message)
                }
                .collect { game ->
                    _state.value = CustomState.Success(game)
                }
        }
    }

    fun checkAnswers(questionList: List<Question>, playersAnswers: List<Int>): Int {
        var correctAnswers = 0
        questionList.forEachIndexed { index, question ->
            val answer = playersAnswers[index]
            if(answer == -1) return@forEachIndexed
            val string = question.correctAnswers?.let { CorrectAnswers::class.memberProperties.toList()[answer].get(it) } as String

            if(string == "true") {
                correctAnswers += 1
            }
        }

        return correctAnswers
    }

    fun saveUserGameStats(gameId: String, lobby: Lobby, user: User, answersList: List<Int>, correctAnswersQuantity: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val points = correctAnswersQuantity * 1
            gameRepository.saveUserGameStats(gameId, lobby, user, answersList, correctAnswersQuantity, points)
        }
    }

    fun setGameFinished() = run { _isGameFinished.value = true }

    fun setUserFinishedGame(gameId: String, lobby: Lobby, user: User, hasFinished: Boolean) {
        viewModelScope.launch {
            gameRepository.setUserFinishedGame(gameId, lobby, user, hasFinished)
        }
    }
}