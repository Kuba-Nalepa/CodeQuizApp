package com.jakubn.codequizapp.ui.game.lobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.domain.model.Lobby
import com.jakubn.codequizapp.domain.model.User
import com.jakubn.codequizapp.domain.usecases.ChangeUserReadinessStatusUseCase
import com.jakubn.codequizapp.domain.usecases.DeleteLobbyUseCase
import com.jakubn.codequizapp.domain.usecases.GetLobbyDataUseCase
import com.jakubn.codequizapp.domain.usecases.RemoveMemberFromLobbyUseCase
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
    private val removeUserFromLobbyUseCase: RemoveMemberFromLobbyUseCase,
    private val deleteLobbyUseCase: DeleteLobbyUseCase,
    private val changeUserReadinessStatusUseCase: ChangeUserReadinessStatusUseCase
) : ViewModel() {
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

    fun removeFromLobby(gameId: String, user: User) {
        if (isCurrentUserFounder(user)) deleteLobby(gameId) else removeMemberFromLobby(gameId)
    }

    fun changeUserReadinessStatus(gameId: String, user: User) {
        viewModelScope.launch {
            val lobbyData = _lobby.value
            lobbyData?.let {
                val newStatus = when (user.uid) {
                    it.founder?.uid -> !it.isFounderReady
                    it.member?.uid -> !it.isMemberReady
                    else -> return@launch
                }

                changeUserReadinessStatusUseCase.changeUserReadinessStatus(gameId, it, user, newStatus)

                if (user.uid == it.founder?.uid) {
                    _lobby.value = it.copy(isFounderReady = newStatus)
                } else if (user.uid == it.member?.uid) {
                    _lobby.value = it.copy(isMemberReady = newStatus)
                }
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
        return user.uid == _lobby.value?.founder?.uid
    }

    fun isCurrentUserMember(user: User): Boolean {
        return user.uid == _lobby.value?.member?.uid
    }

//    wartosc isREady musze pobierac z bazy danych ugh bo przeciez da dwa osobne viewmodele
}