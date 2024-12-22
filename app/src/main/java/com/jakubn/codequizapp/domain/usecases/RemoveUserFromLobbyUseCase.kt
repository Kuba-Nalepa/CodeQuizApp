package com.jakubn.codequizapp.domain.usecases

import com.jakubn.codequizapp.data.repositoryImpl.GameRepositoryImpl
import javax.inject.Inject

data class RemoveUserFromLobbyUseCase @Inject constructor(
    val removeUserFromLobby: RemoveUserFromLobby
) {
    class RemoveUserFromLobby @Inject constructor(private val repository: GameRepositoryImpl) {
        suspend operator fun invoke(gameId: String) = repository.removeUserFromLobby(gameId)
    }
}