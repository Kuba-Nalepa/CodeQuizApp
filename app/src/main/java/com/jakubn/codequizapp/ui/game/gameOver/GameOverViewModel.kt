package com.jakubn.codequizapp.ui.game.gameOver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.domain.model.Game
import com.jakubn.codequizapp.domain.model.Lobby
import com.jakubn.codequizapp.domain.usecases.game.GetGameDataUseCase
import com.jakubn.codequizapp.domain.usecases.user.UpdateUserDataUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameOverViewModel @Inject constructor(
    private val getGameDataUseCase: GetGameDataUseCase,
    private val updateUserDataUseCase: UpdateUserDataUsecase
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

}