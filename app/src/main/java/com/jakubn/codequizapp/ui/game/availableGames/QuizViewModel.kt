package com.jakubn.codequizapp.ui.game.availableGames

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.domain.model.Game
import com.jakubn.codequizapp.domain.usecases.GetGameDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val getGameDataUseCase: GetGameDataUseCase
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
        viewModelScope.launch {
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

    fun startCountdown(onTimerFinished: () -> Unit) {
        viewModelScope.launch {
            while (_countDownValue.value > 0) {
                delay(1000L)
                _countDownValue.value -= 1
            }

            onTimerFinished()
        }
    }

    fun startTimer(duration: Int) {
        viewModelScope.launch {
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
}