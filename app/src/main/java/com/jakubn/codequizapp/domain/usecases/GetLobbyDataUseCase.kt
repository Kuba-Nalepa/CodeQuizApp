package com.jakubn.codequizapp.domain.usecases

import com.jakubn.codequizapp.data.repositoryImpl.GameRepositoryImpl
import javax.inject.Inject

data class GetLobbyDataUseCase @Inject constructor(
    val getLobbyData: GetLobbyData
) {
    class GetLobbyData @Inject constructor(private val repository: GameRepositoryImpl) {
        suspend operator fun invoke(gameId: String) = repository.getLobbyData(gameId)
    }
}