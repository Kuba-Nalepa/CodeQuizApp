package com.jakubn.codequizapp.ui.game.gameOver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.domain.model.Game
import com.jakubn.codequizapp.domain.model.GameResult
import com.jakubn.codequizapp.domain.model.User
import com.jakubn.codequizapp.domain.usecases.game.ListenGameDataChangesUseCase
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
    private val listenGameDataChangesUseCase: ListenGameDataChangesUseCase,
    private val updateUserDataUseCase: UpdateUserDataUsecase,
    private val manageGameState: ManageGameStateUseCase
) : ViewModel() {

    private val _gameResult = MutableStateFlow<GameResult?>(null)
    val gameResult: StateFlow<GameResult?> = _gameResult

    private val _state = MutableStateFlow<CustomState<GameResult>>(CustomState.Idle)
    val state: StateFlow<CustomState<GameResult>> = _state


    fun getGameData(gameId: String, user: User) {
        viewModelScope.launch {
            listenGameDataChangesUseCase.listenGameDataChanges(gameId)
                .onStart { _state.value = CustomState.Loading }
                .catch { e ->
                    _state.value = CustomState.Failure(e.message)
                }
                .collect { game ->
                    handleGameData(game, user)
                }
        }
    }

    private fun handleGameData(game: Game, user: User) {
        val lobby = game.lobby

        val bothFinished = lobby?.hasFounderFinishedGame == true &&
                lobby.hasMemberFinishedGame == true

        if (bothFinished) {
            processGameResults(game, user)
        }
    }

    private fun processGameResults(game: Game, currentUser: User) {
        val lobby = game.lobby
        val questionsSize = game.questions?.size

        val founderPoints = lobby?.founderPoints ?: 0
        val memberPoints = lobby?.memberPoints ?: 0
        val founderAnswers = lobby?.founderCorrectAnswersQuantity ?: 0
        val memberAnswers = lobby?.memberCorrectAnswersQuantity ?: 0

        val result = when {
            founderPoints > memberPoints -> {
                if (currentUser.uid == lobby?.founder?.uid) {
                    questionsSize?.let {
                        GameResult.Win(
                            winner = requireNotNull(lobby?.founder),
                            loser = requireNotNull(lobby?.member),
                            winnerPoints = founderPoints,
                            loserPoints = memberPoints,
                            correctAnswers = founderAnswers,
                            totalQuestions = it
                        )
                    }

                } else {
                    questionsSize?.let {
                        GameResult.Lose(
                            winner = requireNotNull(lobby?.founder),
                            loser = requireNotNull(lobby?.member),
                            winnerPoints = founderPoints,
                            loserPoints = memberPoints,
                            correctAnswers = memberAnswers,
                            totalQuestions = it
                        )
                    }

                }
            }
            memberPoints > founderPoints -> {
                if (currentUser.uid == lobby?.member?.uid) {
                    questionsSize?.let {
                        GameResult.Win(
                            winner = requireNotNull(lobby?.member),
                            loser = requireNotNull(lobby?.founder),
                            winnerPoints = memberPoints,
                            loserPoints = founderPoints,
                            correctAnswers = memberAnswers,
                            totalQuestions = it
                        )
                    }

                } else {
                    questionsSize?.let {
                        GameResult.Lose(
                            winner = requireNotNull(lobby?.member),
                            loser = requireNotNull(lobby?.founder),
                            winnerPoints = memberPoints,
                            loserPoints = founderPoints,
                            correctAnswers = founderAnswers,
                            totalQuestions = it
                        )
                    }
                    }
                }

            else -> {
                questionsSize?.let {
                    GameResult.Tie(
                        firstUser = requireNotNull(lobby?.founder),
                        secondUser = requireNotNull(lobby?.member),
                        firstUserPoints = founderPoints,
                        secondUserPoints = memberPoints,
                        correctAnswers = founderAnswers,
                        totalQuestions = it
                    )
                }
            }
        }

        _gameResult.value = result
        if (result == null) return
        _state.value = CustomState.Success(result)
    }

    fun updateUserData(user: User, score: Int) {
        viewModelScope.launch {
            updateUserDataUseCase.updateUserData(user, score, _gameResult.value is GameResult.Win)
        }
    }

    fun handleGameCleanup(gameId: String) {
        viewModelScope.launch {
            manageGameState.manageGameState(gameId, false)
        }
    }
}