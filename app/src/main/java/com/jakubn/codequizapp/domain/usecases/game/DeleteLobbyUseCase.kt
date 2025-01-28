package com.jakubn.codequizapp.domain.usecases.game

import com.jakubn.codequizapp.data.repositoryImpl.GameRepositoryImpl
import javax.inject.Inject

data class DeleteLobbyUseCase @Inject constructor(
    val deleteLobby: DeleteLobby
) {
    class DeleteLobby @Inject constructor(private val repository: GameRepositoryImpl) {
        suspend operator fun invoke(gameId: String) = repository.deleteLobby(gameId)
    }
}