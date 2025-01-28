package com.jakubn.codequizapp.ui.game.availableGames

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.domain.model.Game
import com.jakubn.codequizapp.domain.model.User
import com.jakubn.codequizapp.domain.usecases.game.AddUserToLobbyUseCase
import com.jakubn.codequizapp.domain.usecases.game.GetGamesListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AvailableGamesViewModel @Inject constructor(
    private val getGamesListUseCase: GetGamesListUseCase,
    private val addUserToLobbyUseCase: AddUserToLobbyUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<CustomState<List<Game>>>(CustomState.Idle)
    val state: StateFlow<CustomState<List<Game>>> = _state

    fun getGamesList() {
        viewModelScope.launch {
            getGamesListUseCase.getGamesList.invoke()
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
            addUserToLobbyUseCase.addUserToLobby.invoke(gameId, user)

        }
    }
}