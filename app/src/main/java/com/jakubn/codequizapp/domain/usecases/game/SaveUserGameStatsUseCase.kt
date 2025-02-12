package com.jakubn.codequizapp.domain.usecases.game

import com.jakubn.codequizapp.data.repositoryImpl.GameRepositoryImpl
import com.jakubn.codequizapp.domain.model.Lobby
import com.jakubn.codequizapp.domain.model.User
import javax.inject.Inject

data class SaveUserGameStatsUseCase @Inject constructor(
    val saveUserGameStats: SaveUserGameStats
) {

    class SaveUserGameStats @Inject constructor(private val gameRepositoryImpl: GameRepositoryImpl) {
        suspend operator fun invoke(gameId: String, lobby: Lobby, user: User, userAnswers: List<Int>, correctAnswersQuantity: Int, points: Int)
        = gameRepositoryImpl.saveUserGameStats(gameId, lobby, user, userAnswers, correctAnswersQuantity, points)

    }
}