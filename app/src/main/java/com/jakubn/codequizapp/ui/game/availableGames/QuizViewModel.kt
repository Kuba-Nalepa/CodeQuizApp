package com.jakubn.codequizapp.ui.game.availableGames

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.domain.model.Game
import com.jakubn.codequizapp.domain.usecases.GetGameDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _countDownValue = MutableStateFlow(5) // Initial countdown value
    val countDownValue: StateFlow<Int> = _countDownValue

    private val _isCounterFinished = MutableStateFlow(false)
    val isCounterFinished: StateFlow<Boolean> = _isCounterFinished

    private val _isGameFinished = MutableStateFlow(false)
    val isGameFinished: StateFlow<Boolean> = _isGameFinished

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

    fun setCounterFinished() = run { _isCounterFinished.value = true }

    fun setGameFinished() = run { _isGameFinished.value = true }
}