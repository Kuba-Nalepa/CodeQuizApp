package com.jakubn.codequizapp.domain.usecases

import com.jakubn.codequizapp.data.repositoryImpl.GameRepositoryImpl
import javax.inject.Inject

data class RemoveMemberFromLobbyUseCase @Inject constructor(
    val removeMemberFromLobby: RemoveMemberFromLobby
) {
    class RemoveMemberFromLobby @Inject constructor(private val repository: GameRepositoryImpl) {
        suspend operator fun invoke(gameId: String) = repository.removeMemberFromLobby(gameId)
    }
}