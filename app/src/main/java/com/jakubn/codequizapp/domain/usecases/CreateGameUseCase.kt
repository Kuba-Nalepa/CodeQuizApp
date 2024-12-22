package com.jakubn.codequizapp.domain.usecases

import com.jakubn.codequizapp.data.repositoryImpl.GameRepositoryImpl
import com.jakubn.codequizapp.domain.model.User
import javax.inject.Inject

data class CreateGameUseCase @Inject constructor(
    val createGame: CreateGame
) {

    class CreateGame @Inject constructor(private val repository: GameRepositoryImpl) {
        suspend operator fun invoke(
            questionCategory: String,
            questionQuantity: Int,
            questionDuration: Int,
            founder: User
        ) = repository.createGame(questionCategory, questionQuantity, questionDuration, founder)
    }
}
