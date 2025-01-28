package com.jakubn.codequizapp.domain.usecases.game

import com.jakubn.codequizapp.data.repositoryImpl.GameRepositoryImpl
import com.jakubn.codequizapp.domain.model.Lobby
import com.jakubn.codequizapp.domain.model.User
import javax.inject.Inject

data class ChangeUserReadinessStatusUseCase @Inject constructor(
    val changeUserReadinessStatus: ChangeUserReadinessStatus
) {
    class ChangeUserReadinessStatus @Inject constructor(private val repository: GameRepositoryImpl) {
        suspend operator fun invoke(
            gameId: String,
            lobby: Lobby,
            user: User,
            userReadinessStatus: Boolean
        ) = repository.changeUserReadinessStatus(gameId, lobby, user, userReadinessStatus)
    }
}