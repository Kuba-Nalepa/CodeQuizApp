package com.jakubn.codequizapp.ui.createGame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.domain.usecases.CreateGameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CreateGameViewModel @Inject constructor(
    private val createGameUseCase: CreateGameUseCase
): ViewModel() {
    private val _state = MutableStateFlow<CustomState<Boolean>>(CustomState.Idle)
    val state: StateFlow<CustomState<Boolean>> = _state

    fun createGame(
        questionCategory: String,
        questionQuantity: Int,
        questionDuration: Int
    ) {

        viewModelScope.launch {
            createGameUseCase.createGame(questionCategory, questionQuantity, questionDuration)
                .onStart {
                    _state.value = CustomState.Loading
                }
                .catch { throwable ->
                    _state.value = CustomState.Failure(throwable.message)
                }
                .collect { boolean ->
                    _state.value = CustomState.Success(boolean)
                }
        }
    }

    fun resetState() {
        _state.value = CustomState.Idle
    }
}