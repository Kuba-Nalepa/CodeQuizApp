package com.jakubn.codequizapp.domain.usecases.game

import com.jakubn.codequizapp.data.repositoryImpl.GameRepositoryImpl
import javax.inject.Inject

data class StartGameUseCase @Inject constructor(
    val startGame: StartGame
) {
    class StartGame @Inject constructor(private val repository: GameRepositoryImpl) {
        suspend operator fun invoke(gameId: String) = repository.startGame(gameId)
    }
}