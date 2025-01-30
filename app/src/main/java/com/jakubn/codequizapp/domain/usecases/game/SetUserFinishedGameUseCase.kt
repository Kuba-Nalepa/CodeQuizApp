package com.jakubn.codequizapp.domain.usecases.game

import com.jakubn.codequizapp.data.repositoryImpl.GameRepositoryImpl
import com.jakubn.codequizapp.domain.model.Lobby
import com.jakubn.codequizapp.domain.model.User
import javax.inject.Inject

data class SetUserFinishedGameUseCase @Inject constructor(
    val setUserFinishedGame: SetUserFinishedGame
) {

    class SetUserFinishedGame @Inject constructor(private val gameRepositoryImpl: GameRepositoryImpl) {
        suspend operator fun invoke(gameId: String, lobby: Lobby, user: User, hasFinished: Boolean)
                = gameRepositoryImpl.setUserFinishedGame(gameId, lobby, user, hasFinished)

    }
}