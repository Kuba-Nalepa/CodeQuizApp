package com.jakubn.codequizapp.ui.game.quizReviewScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.domain.model.CorrectAnswers
import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.domain.model.Game
import com.jakubn.codequizapp.domain.model.Question
import com.jakubn.codequizapp.domain.model.User
import com.jakubn.codequizapp.domain.usecases.game.GetGameDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.full.memberProperties

@HiltViewModel
class QuizReviewViewModel @Inject constructor(
    private val getGameDataUseCase: GetGameDataUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<CustomState<Game?>>(CustomState.Idle)
    val state: StateFlow<CustomState<Game?>> = _state


    fun getGameData(gameId: String) {
        viewModelScope.launch {
            getGameDataUseCase.getGameData(gameId)
                .onStart {
                    _state.value = CustomState.Loading
                }
                .catch { throwable ->
                    _state.value = CustomState.Failure(throwable.message ?: "Unknown error")
                }
                .collect { game ->
                    _state.value = CustomState.Success(game)
                }
        }
    }

    fun correctIndex(question: Question): Int {
        val correctAnswersList = CorrectAnswers::class.memberProperties.toList()

        question.correctAnswers?.let {
            correctAnswersList.forEachIndexed { index, answer ->

                if (answer.get(it) == "true") {
                    return index
                }
            }
        }

        return -1
    }

    fun getUserAnswersList(user: User): List<Int>? {
        val game = (_state.value as? CustomState.Success) ?: return null

        val lobby = game.result?.lobby ?: return null

        return when (user.uid) {
            lobby.founder?.uid -> lobby.founderAnswersList
            lobby.member?.uid -> lobby.memberAnswersList
            else -> null
        }
    }

}