package com.jakubn.codequizapp.domain.usecases.game

import com.jakubn.codequizapp.data.repositoryImpl.GameRepositoryImpl
import javax.inject.Inject

data class GetGameDataUseCase @Inject constructor(
    val getGameData: GetGameData
) {
    class GetGameData @Inject constructor(private val repository: GameRepositoryImpl) {
        suspend operator fun invoke(gameId: String) = repository.getGameData(gameId)
    }
}