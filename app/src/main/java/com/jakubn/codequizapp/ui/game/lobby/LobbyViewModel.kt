package com.jakubn.codequizapp.ui.game.lobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.domain.model.Lobby
import com.jakubn.codequizapp.domain.model.User
import com.jakubn.codequizapp.domain.usecases.DeleteLobbyUseCase
import com.jakubn.codequizapp.domain.usecases.GetLobbyDataUseCase
import com.jakubn.codequizapp.domain.usecases.RemoveUserFromLobbyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LobbyViewModel @Inject constructor(
    private val getLobbyDataUseCase: GetLobbyDataUseCase,
    private val removeUserFromLobbyUseCase: RemoveUserFromLobbyUseCase,
    private val deleteLobbyUseCase: DeleteLobbyUseCase
): ViewModel() {
    private val _state = MutableStateFlow<CustomState<Lobby?>>(CustomState.Idle)
    val state: StateFlow<CustomState<Lobby?>> = _state

    private val _lobby = MutableStateFlow<Lobby?>(Lobby())
    val lobby: StateFlow<Lobby?> = _lobby

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
                    _lobby.value = lobby
                    _state.value = CustomState.Success(lobby)
                }
        }
    }


    fun removeUserFromLobby(gameId: String) {
        viewModelScope.launch {
            removeUserFromLobbyUseCase.removeUserFromLobby.invoke(gameId)
        }
    }

    fun deleteLobby(gameId: String) {
        viewModelScope.launch {
            deleteLobbyUseCase.deleteLobby.invoke(gameId)
        }
    }

    fun isCurrentUserFounder(user: User): Boolean {
        return user.uid == _lobby.value?.founder?.uid
    }
}