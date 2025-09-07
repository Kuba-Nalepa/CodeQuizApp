package com.jakubn.codequizapp.ui.game.lobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.data.repositoryImpl.GameRepository
import com.jakubn.codequizapp.data.repositoryImpl.UserDataRepository
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.model.Friend
import com.jakubn.codequizapp.model.Game
import com.jakubn.codequizapp.model.Lobby
import com.jakubn.codequizapp.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LobbyViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val userDataRepository: UserDataRepository
) : ViewModel() {

    private val _state = MutableStateFlow<CustomState<Game?>>(CustomState.Idle)
    val state: StateFlow<CustomState<Game?>> = _state

    private val _lobby = MutableStateFlow<CustomState<Lobby?>>(CustomState.Idle)
    val lobby: StateFlow<CustomState<Lobby?>> = _lobby

    private val _friendsState = MutableStateFlow<CustomState<List<Friend>>>(CustomState.Idle)
    val friendsState: StateFlow<CustomState<List<Friend>>> = _friendsState.asStateFlow()

    fun getGameData(gameId: String) {
        viewModelScope.launch {
            gameRepository.listenGameDataChanges(gameId)
                .onStart { _state.value = CustomState.Loading }
                .catch { e -> _state.value = CustomState.Failure(e.message) }
                .collect { game ->
                    _lobby.value = CustomState.Success(game.lobby)
                    _state.value = CustomState.Success(game)
                }
        }
    }

    fun getFriends(userId: String) {
        viewModelScope.launch {
            _friendsState.value = CustomState.Loading
            try {
                val friends = userDataRepository.getUsersFriends(userId)
                _friendsState.value = CustomState.Success(friends)
            } catch (e: Exception) {
                _friendsState.value = CustomState.Failure(e.message)
            }
        }
    }

    fun inviteFriend(gameId: String, friendId: String) {
        viewModelScope.launch {
            // TODO gameInvitation business logic
            println("Invitation sent to friend: $friendId for game: $gameId")
        }
    }

    fun leaveFromLobby(gameId: String, user: User) {
        viewModelScope.launch {
            if (isCurrentUserFounder(user)) {
                gameRepository.deleteLobby(gameId)
            } else {
                gameRepository.removeMemberFromLobby(gameId)
            }
        }
    }

    fun setUserLeftGame(game: Game?, user: User) {
        viewModelScope.launch {
            if (game != null) {
                gameRepository.setUserLeftGame(game, user, true)
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

                    gameRepository.changeUserReadinessStatus(
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
            gameRepository.manageGameState(gameId, true)
            _state.value = (_state.value as? CustomState.Success)?.let {
                it.copy(result = it.result?.copy(gameInProgress = true))
            } ?: return@launch
        }
    }

    fun isCurrentUserFounder(user: User): Boolean = checkUserRole(user) { it.founder?.uid }
    fun isCurrentUserMember(user: User): Boolean = checkUserRole(user) { it.member?.uid }

    fun isMemberReady(): Boolean? {
        val lobby = (lobby.value as? CustomState.Success)?.result
        return if (lobby?.member != null) lobby.isMemberReady else null
    }

    private inline fun checkUserRole(user: User, crossinline roleSelector: (Lobby) -> String?) =
        (_lobby.value as? CustomState.Success)?.result?.let { lobby ->
            user.uid == roleSelector(lobby)
        } ?: false
}