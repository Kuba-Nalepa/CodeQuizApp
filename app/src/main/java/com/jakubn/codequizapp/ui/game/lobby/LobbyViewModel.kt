package com.jakubn.codequizapp.ui.game.lobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.domain.model.Lobby
import com.jakubn.codequizapp.domain.usecases.GetLobbyDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LobbyViewModel @Inject constructor(
    private val getLobbyDataUseCase: GetLobbyDataUseCase
): ViewModel() {
    private val _state = MutableStateFlow<CustomState<Lobby>>(CustomState.Idle)
    val state: StateFlow<CustomState<Lobby>> = _state

    fun getLobbyData(gameId: String) {
        viewModelScope.launch {
            getLobbyDataUseCase.getLobbyData(gameId)
                .onStart {
                    _state.value = CustomState.Loading
                }
                .catch { throwable ->
                    _state.value = CustomState.Failure(throwable.message)
                }
                .collect { lobby ->
                    _state.value = CustomState.Success(lobby)
                }
        }
    }
}