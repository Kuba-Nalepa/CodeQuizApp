package com.jakubn.codequizapp.ui.game.gameOver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.domain.model.Game
import com.jakubn.codequizapp.domain.model.Lobby
import com.jakubn.codequizapp.domain.model.User
import com.jakubn.codequizapp.domain.usecases.game.GetGameDataUseCase
import com.jakubn.codequizapp.domain.usecases.game.ManageGameStateUseCase
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
    private val updateUserDataUseCase: UpdateUserDataUsecase,
    private val manageGameState: ManageGameStateUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<CustomState<Game?>>(CustomState.Idle)
    val state: StateFlow<CustomState<Game?>> = _state

    private val _lobby = MutableStateFlow<CustomState<Lobby?>>(CustomState.Idle)
    val lobby: StateFlow<CustomState<Lobby?>> = _lobby

    private val _haveUsersFinishedGame = MutableStateFlow(false)
    val haveUsersFinishedGame: StateFlow<Boolean> = _haveUsersFinishedGame

    fun getGameData(gameId: String) {
        viewModelScope.launch {
            getGameDataUseCase.getGameData(gameId)
                .onStart { _state.value = CustomState.Loading }
                .catch { throwable ->
                    _state.value = CustomState.Failure(throwable.message)
                }
                .collect { game ->
                    _lobby.value = CustomState.Success(game?.lobby)
                    _state.value = CustomState.Success(game)
                }
        }
    }

    fun updateUserData(user: User, score: Int) {
        viewModelScope.launch {
            if (_state.value !is CustomState.Success) return@launch
            val hasWon = hasCurrentUserWon(user)
            updateUserDataUseCase.updateUserData(user, score, hasWon)
        }
    }

    fun changeGameInProgressStatus(gameId: String, state: Boolean) {
        viewModelScope.launch {
            manageGameState.manageGameState(gameId, state)
        }
    }

    fun haveUsersFinishedGame(): Boolean {
        val lobby = (_lobby.value as? CustomState.Success)?.result
        if(lobby?.hasMemberFinishedGame == true && lobby.hasFounderFinishedGame == true) {
            _haveUsersFinishedGame.value = true
            return true
        }

        return false
    }

    fun getUserScore(user: User): Int? {
        val lobby = (_lobby.value as? CustomState.Success)?.result
        return if (isCurrentUserFounder(user)) lobby?.founderPoints else lobby?.memberPoints
    }

    fun hasCurrentUserWon(user: User): Boolean {
        val lobbyState = _lobby.value
        if(lobbyState is CustomState.Success) {
            val winner = determineWinner(lobbyState.result)
            return winner?.uid == user.uid

        }

        return false
    }

    fun determineWinner(lobby: Lobby?): User? {
        if (lobby == null) return null
        
        val founderPoints = lobby.founderPoints ?: 0
        val memberPoints = lobby.memberPoints ?: 0

        return when {
            founderPoints > memberPoints -> lobby.founder
            founderPoints < memberPoints -> lobby.member
            else -> null // It's a tie
        }
    }

    fun determineLoser(lobby: Lobby?): User? {
        if (lobby == null) return null
        val founderPoints = lobby.founderPoints ?: 0
        val memberPoints = lobby.memberPoints ?: 0

        return when {
            founderPoints > memberPoints -> lobby.member
            founderPoints < memberPoints -> lobby.founder
            else -> null // It's a tie
        }
    }

    fun getWinnerPoints(lobby: Lobby?): Int? {
        if (lobby == null) return null

        val founderPoints = lobby.founderPoints ?: 0
        val memberPoints = lobby.memberPoints ?: 0

        return when {
            founderPoints > memberPoints -> founderPoints
            founderPoints < memberPoints -> memberPoints
            else -> null // It's a tie
        }
    }

    fun getLoserPoints(lobby: Lobby?): Int? {
        if (lobby == null) return null


        val founderPoints = lobby.founderPoints ?: 0
        val memberPoints = lobby.memberPoints ?: 0

        return when {
            founderPoints > memberPoints -> memberPoints
            founderPoints < memberPoints -> founderPoints
            else -> null // It's a tie
        }
    }

    fun getCorrectAnswersQuantity(lobby: Lobby?): Int? {
        if (lobby == null) return null

        val founderCorrectAnswersQuantity = lobby.founderCorrectAnswersQuantity ?: 0
        val memberCorrectAnswersQuantity = lobby.memberCorrectAnswersQuantity ?: 0

        return when {
            founderCorrectAnswersQuantity > memberCorrectAnswersQuantity -> founderCorrectAnswersQuantity
            founderCorrectAnswersQuantity < memberCorrectAnswersQuantity -> memberCorrectAnswersQuantity
            else -> null // It's a tie
        }
    }

    private fun isCurrentUserFounder(user: User): Boolean {
        val lobby = (_lobby.value as? CustomState.Success)?.result
        return lobby?.founder?.uid == user.uid
    }
}