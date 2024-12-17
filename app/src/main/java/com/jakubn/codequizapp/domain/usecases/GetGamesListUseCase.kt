package com.jakubn.codequizapp.domain.usecases

import com.jakubn.codequizapp.domain.repositoryImpl.GameRepositoryImpl
import javax.inject.Inject

data class GetGamesListUseCase @Inject constructor(
    val getGamesList: GetGamesList
) {
    class GetGamesList @Inject constructor(private val repository: GameRepositoryImpl) {
        suspend operator fun invoke() = repository.getGamesList()
    }
}