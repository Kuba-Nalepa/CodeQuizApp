package com.jakubn.codequizapp.domain.usecases.game

import com.jakubn.codequizapp.data.repositoryImpl.GameRepositoryImpl
import com.jakubn.codequizapp.domain.model.Lobby
import com.jakubn.codequizapp.domain.model.User
import javax.inject.Inject

data class SaveUserGamePointsUseCase @Inject constructor(
    val saveUserGamePoints: SaveUserGamePoints
) {

    class SaveUserGamePoints @Inject constructor(private val gameRepositoryImpl: GameRepositoryImpl) {
        suspend operator fun invoke(gameId: String, lobby: Lobby, user: User, points: Int)
        = gameRepositoryImpl.saveUserGamePoints(gameId, lobby, user, points)

    }
}