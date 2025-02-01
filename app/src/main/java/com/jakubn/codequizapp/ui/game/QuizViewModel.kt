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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    private val _countDownValue = MutableStateFlow(5)
    val countDownValue: StateFlow<Int> = _countDownValue

    private val _isCounterFinished = MutableStateFlow(false)
    val isCounterFinished: StateFlow<Boolean> = _isCounterFinished

    private val _isGameFinished = MutableStateFlow(false)
    val isGameFinished: StateFlow<Boolean> = _isGameFinished

    private val _timerProgress = MutableStateFlow(1f)
    val timerProgress: StateFlow<Float> = _timerProgress

    private val _isTimeUp = MutableStateFlow(false)
    val isTimeUp: StateFlow<Boolean> = _isTimeUp

    private var timerJob: Job? = null

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

    fun startCountdown(onTimerFinished: () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            while (_countDownValue.value > 0) {
                delay(1000L)
                _countDownValue.value -= 1
            }

            onTimerFinished()
        }
    }

    fun startTimer(duration: Int) {
        viewModelScope.launch(Dispatchers.Main) {
            _isTimeUp.value = false
            _timerProgress.value = 0f

            timerJob = viewModelScope.launch {
                val tickInterval = 100L
                val totalTicks = duration * 1000 / tickInterval
                for (tick in 0 .. totalTicks) {
                    _timerProgress.value = tick.toFloat() / totalTicks
                    delay(tickInterval)
                }
                _isTimeUp.value = true
            }
        }
    }

    fun resetTimer() {
        timerJob?.cancel()
        _timerProgress.value = 1f
        _isTimeUp.value = false
    }

    fun setCounterFinished() = run { _isCounterFinished.value = true }

    fun setGameFinished() = run { _isGameFinished.value = true }

    fun setUserFinishedGame(gameId: String, lobby: Lobby, user: User, hasFinished: Boolean) {
        viewModelScope.launch {
            setUserFinishedGameUseCase.setUserFinishedGame.invoke(gameId, lobby, user, hasFinished)
        }
    }
}