package com.jakubn.codequizapp.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.domain.model.CorrectAnswers
import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.domain.model.Game
import com.jakubn.codequizapp.domain.model.Lobby
import com.jakubn.codequizapp.domain.model.Question
import com.jakubn.codequizapp.domain.model.User
import com.jakubn.codequizapp.domain.usecases.game.GetGameDataUseCase
import com.jakubn.codequizapp.domain.usecases.game.SaveUserGamePointsUseCase
import com.jakubn.codequizapp.domain.usecases.game.SetUserFinishedGameUseCase
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
    private val getGameDataUseCase: GetGameDataUseCase,
    private val saveUserGamePointsUseCase: SaveUserGamePointsUseCase,
    private val setUserFinishedGameUseCase: SetUserFinishedGameUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<CustomState<Game?>>(CustomState.Idle)
    val state: StateFlow<CustomState<Game?>> = _state

    private val _isGameFinished = MutableStateFlow(false)
    val isGameFinished: StateFlow<Boolean> = _isGameFinished

    fun getGameData(gameId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getGameDataUseCase.getGameData(gameId)
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

    fun saveUserGamePoints(gameId: String, lobby: Lobby, user: User, correctAnswersQuantity: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val points = correctAnswersQuantity * 10
            saveUserGamePointsUseCase.saveUserGamePoints(gameId, lobby, user, correctAnswersQuantity, points)
        }
    }

    fun setGameFinished() = run { _isGameFinished.value = true }

    fun setUserFinishedGame(gameId: String, lobby: Lobby, user: User, hasFinished: Boolean) {
        viewModelScope.launch {
            setUserFinishedGameUseCase.setUserFinishedGame.invoke(gameId, lobby, user, hasFinished)
        }
    }
}