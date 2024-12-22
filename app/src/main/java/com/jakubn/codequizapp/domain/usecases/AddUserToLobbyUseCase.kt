package com.jakubn.codequizapp.domain.usecases

import com.jakubn.codequizapp.domain.model.User
import com.jakubn.codequizapp.data.repositoryImpl.GameRepositoryImpl
import javax.inject.Inject

data class AddUserToLobbyUseCase @Inject constructor(
    val addUserToLobby: AddUserToLobby
) {

    class AddUserToLobby @Inject constructor(private val repository: GameRepositoryImpl) {
        suspend operator fun invoke(
            gameId: String,
            user: User
        ) = repository.addUserToLobby(gameId, user)
    }
}