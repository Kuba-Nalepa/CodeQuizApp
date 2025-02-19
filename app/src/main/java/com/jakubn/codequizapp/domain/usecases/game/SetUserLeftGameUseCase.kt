package com.jakubn.codequizapp.domain.usecases.game

import com.jakubn.codequizapp.data.repositoryImpl.GameRepositoryImpl
import com.jakubn.codequizapp.domain.model.Game
import com.jakubn.codequizapp.domain.model.User
import javax.inject.Inject

data class SetUserLeftGameUseCase @Inject constructor(
    val setUserLeftGame: SetUserLeftGame
) {

    class SetUserLeftGame @Inject constructor(private val gameRepository: GameRepositoryImpl) {
        suspend operator fun invoke(game: Game, user: User, hasLeft: Boolean)
                = gameRepository.setUserLeftGame(game, user, hasLeft)

    }
}