package com.jakubn.codequizapp.domain.usecases.game

import com.jakubn.codequizapp.data.repositoryImpl.GameRepositoryImpl
import javax.inject.Inject

data class ManageGameStateUseCase @Inject constructor(
    val manageGameState: ManageGameState
) {
    class ManageGameState @Inject constructor(private val repository: GameRepositoryImpl) {
        suspend operator fun invoke(gameId: String, state: Boolean) = repository.manageGameState(gameId, state)
    }
}