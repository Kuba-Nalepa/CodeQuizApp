package com.jakubn.codequizapp.ui.game.availableGames

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.data.repositoryImpl.GameRepository
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.model.Game
import com.jakubn.codequizapp.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AvailableGamesViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _state = MutableStateFlow<CustomState<List<Game>>>(CustomState.Idle)
    val state: StateFlow<CustomState<List<Game>>> = _state

    fun getGamesList() {
        viewModelScope.launch {
            gameRepository.getGamesList()
                .onStart {
                    _state.value = CustomState.Loading
                }
                .catch { message ->
                    _state.value = CustomState.Failure(message.message)
                }
                .collect { games ->
                    _state.value = CustomState.Success(games)
                }
        }
    }

    fun addUserToLobby(gameId: String, user: User) {
        viewModelScope.launch {
//            addUserToLobbyUseCase.addUserToLobby.invoke(gameId, user)
            gameRepository.addMemberToLobby(gameId, user)

        }
    }
}