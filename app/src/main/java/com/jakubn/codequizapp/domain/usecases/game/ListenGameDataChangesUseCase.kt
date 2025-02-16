package com.jakubn.codequizapp.domain.usecases.game

import com.jakubn.codequizapp.data.repositoryImpl.GameRepositoryImpl
import javax.inject.Inject

data class ListenGameDataChangesUseCase @Inject constructor(
    val listenGameDataChanges: ListenGameDataChanges
) {
    class ListenGameDataChanges @Inject constructor(private val repository: GameRepositoryImpl) {
        suspend operator fun invoke(gameId: String) = repository.listenGameDataChanges(gameId)
    }
}