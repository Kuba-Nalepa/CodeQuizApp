package com.jakubn.codequizapp.ui.game.lobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.domain.model.Game
import com.jakubn.codequizapp.domain.model.Lobby
import com.jakubn.codequizapp.domain.model.User
import com.jakubn.codequizapp.domain.usecases.ChangeUserReadinessStatusUseCase
import com.jakubn.codequizapp.domain.usecases.DeleteLobbyUseCase
import com.jakubn.codequizapp.domain.usecases.GetGameDataUseCase
import com.jakubn.codequizapp.domain.usecases.RemoveMemberFromLobbyUseCase
import com.jakubn.codequizapp.domain.usecases.StartGameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LobbyViewModel @Inject constructor(
    private val getGameDataUseCase: GetGameDataUseCase,
    private val removeUserFromLobbyUseCase: RemoveMemberFromLobbyUseCase,
    private val deleteLobbyUseCase: DeleteLobbyUseCase,
    private val changeUserReadinessStatusUseCase: ChangeUserReadinessStatusUseCase,
    private val startGameUseCase: StartGameUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<CustomState<Game?>>(CustomState.Idle)
    val state: StateFlow<CustomState<Game?>> = _state

    private val _lobby = MutableStateFlow<CustomState<Lobby?>>(CustomState.Idle)
    val lobby: StateFlow<CustomState<Lobby?>> = _lobby

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
                    _lobby.value = CustomState.Success(game?.lobby)
                    _state.value = CustomState.Success(game)
                }
        }
    }

    fun removeFromLobby(gameId: String, user: User) {
        if (isCurrentUserFounder(user)) deleteLobby(gameId) else removeMemberFromLobby(gameId)
    }

    fun changeUserReadinessStatus(gameId: String, user: User) {
        viewModelScope.launch {
            val lobbyState = _lobby.value

            if (lobbyState !is CustomState.Success || lobbyState.result == null) {
                return@launch
            }

            val lobbyData = lobbyState.result

            val newStatus = when (user.uid) {
                lobbyData.founder?.uid -> !lobbyData.isFounderReady
                lobbyData.member?.uid -> !lobbyData.isMemberReady
                else -> return@launch
            }

            changeUserReadinessStatusUseCase.changeUserReadinessStatus(gameId, lobbyData, user, newStatus)

            val updatedLobby = if (user.uid == lobbyData.founder?.uid) {
                lobbyData.copy(isFounderReady = newStatus)
            } else {
                lobbyData.copy(isMemberReady = newStatus)
            }
            _lobby.value = CustomState.Success(updatedLobby)
        }
    }

    fun startGame(gameId: String) {
        val gameState = _state.value

        viewModelScope.launch {
            startGameUseCase.startGame.invoke(gameId)

            if(gameState is CustomState.Success && gameState.result != null) {
                gameState.result.isGameStarted = true
            }
        }
    }


    private fun removeMemberFromLobby(gameId: String) {
        viewModelScope.launch {
            removeUserFromLobbyUseCase.removeMemberFromLobby.invoke(gameId)
        }
    }

    private fun deleteLobby(gameId: String) {
        viewModelScope.launch {
            deleteLobbyUseCase.deleteLobby.invoke(gameId)
        }
    }

    fun isCurrentUserFounder(user: User): Boolean {
        val lobbyState = _lobby.value

        return if (lobbyState is CustomState.Success && lobbyState.result != null) {
            user.uid == lobbyState.result.founder?.uid
        } else {
            false
        }
    }

    fun isCurrentUserMember(user: User): Boolean {
        val lobbyState = _lobby.value

        return if (lobbyState is CustomState.Success && lobbyState.result != null) {
            user.uid == lobbyState.result.member?.uid
        } else {
            false
        }
    }
}