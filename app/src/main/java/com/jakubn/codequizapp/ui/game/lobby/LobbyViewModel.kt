package com.jakubn.codequizapp.ui.game.lobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.domain.model.Game
import com.jakubn.codequizapp.domain.model.Lobby
import com.jakubn.codequizapp.domain.model.User
import com.jakubn.codequizapp.domain.usecases.game.ChangeUserReadinessStatusUseCase
import com.jakubn.codequizapp.domain.usecases.game.DeleteLobbyUseCase
import com.jakubn.codequizapp.domain.usecases.game.GetGameDataUseCase
import com.jakubn.codequizapp.domain.usecases.game.ManageGameStateUseCase
import com.jakubn.codequizapp.domain.usecases.game.RemoveMemberFromLobbyUseCase
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
    private val manageGameStateUseCase: ManageGameStateUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<CustomState<Game?>>(CustomState.Idle)
    val state: StateFlow<CustomState<Game?>> = _state

    private val _lobby = MutableStateFlow<CustomState<Lobby?>>(CustomState.Idle)
    val lobby: StateFlow<CustomState<Lobby?>> = _lobby

    fun getGameData(gameId: String) {
        viewModelScope.launch {
            getGameDataUseCase.getGameData(gameId)
                .onStart { _state.value = CustomState.Loading }
                .catch { e -> _state.value = CustomState.Failure(e.message) }
                .collect { game ->
                    _lobby.value = CustomState.Success(game?.lobby)
                    _state.value = CustomState.Success(game)
                }
        }
    }

    fun removeFromLobby(gameId: String, user: User) {
        viewModelScope.launch {
            if (isCurrentUserFounder(user)) {
                deleteLobbyUseCase.deleteLobby(gameId)
            } else {
                removeUserFromLobbyUseCase.removeMemberFromLobby(gameId)
            }
        }
    }

    fun changeUserReadinessStatus(gameId: String, user: User) {
        viewModelScope.launch {
            when (val lobbyState = _lobby.value) {
                is CustomState.Success -> lobbyState.result?.let { lobby ->
                    val newStatus = when (user.uid) {
                        lobby.founder?.uid -> !lobby.isFounderReady
                        lobby.member?.uid -> !lobby.isMemberReady
                        else -> return@launch
                    }

                    changeUserReadinessStatusUseCase.changeUserReadinessStatus(
                        gameId, lobby, user, newStatus
                    )

                    _lobby.value = CustomState.Success(
                        lobby.copy(
                            isFounderReady = if (user.uid == lobby.founder?.uid) newStatus else lobby.isFounderReady,
                            isMemberReady = if (user.uid == lobby.member?.uid) newStatus else lobby.isMemberReady
                        )
                    )
                }
                else -> Unit
            }
        }
    }

    fun startGame(gameId: String) {
        viewModelScope.launch {
            manageGameStateUseCase.manageGameState(gameId, true)
            _state.value = (_state.value as? CustomState.Success)?.let {
                it.copy(result = it.result?.copy(gameInProgress = true))
            } ?: return@launch
        }
    }

    fun isCurrentUserFounder(user: User): Boolean = checkUserRole(user) { it.founder?.uid }
    fun isCurrentUserMember(user: User): Boolean = checkUserRole(user) { it.member?.uid }

    private inline fun checkUserRole(user: User, crossinline roleSelector: (Lobby) -> String?) =
        (_lobby.value as? CustomState.Success)?.result?.let { lobby ->
            user.uid == roleSelector(lobby)
        } ?: false
}