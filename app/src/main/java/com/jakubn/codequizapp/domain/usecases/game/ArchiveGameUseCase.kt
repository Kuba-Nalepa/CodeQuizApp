package com.jakubn.codequizapp.domain.usecases.game

import com.jakubn.codequizapp.data.repositoryImpl.GameRepositoryImpl
import com.jakubn.codequizapp.domain.model.Game
import javax.inject.Inject

data class ArchiveGameUseCase  @Inject constructor(
    val archiveGame: ArchiveGame
) {
    class ArchiveGame @Inject constructor(private val repository: GameRepositoryImpl) {
        suspend operator fun invoke(game: Game) = repository.archiveGame(game)
    }
}