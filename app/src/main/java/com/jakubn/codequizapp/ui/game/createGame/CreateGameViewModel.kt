package com.jakubn.codequizapp.ui.game.createGame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.domain.model.User
import com.jakubn.codequizapp.domain.usecases.game.CreateGameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CreateGameViewModel @Inject constructor(
    private val createGameUseCase: CreateGameUseCase,
) : ViewModel() {
    private val _createGameState = MutableStateFlow<CustomState<String>>(CustomState.Idle)
    val createGameState: StateFlow<CustomState<String>> = _createGameState

    fun createGame(
        questionCategory: String,
        questionQuantity: Int,
        questionDuration: Int,
        founder: User
    ) {

        viewModelScope.launch {
            createGameUseCase.createGame(questionCategory, questionQuantity, questionDuration, founder)
                .onStart {
                    _createGameState.value = CustomState.Loading
                }
                .catch { throwable ->
                    _createGameState.value = CustomState.Failure(throwable.message)
                }
                .collect { gameId ->
                    _createGameState.value = CustomState.Success(gameId)
                }
        }
    }

    fun resetState() {
        _createGameState.value = CustomState.Idle
    }
}