package com.jakubn.codequizapp.data.repository

import com.jakubn.codequizapp.domain.model.Game
import com.jakubn.codequizapp.domain.model.Lobby
import com.jakubn.codequizapp.domain.model.User
import kotlinx.coroutines.flow.Flow


interface GameRepository {

    suspend fun createGame(questionCategory: String, questionQuantity: Int, questionDuration: Int, founder: User): Flow<String>

    suspend fun getLobbyData(gameId: String): Flow<Lobby?>

    suspend fun addUserToLobby(gameId: String, user: User)

    suspend fun removeUserFromLobby(gameId: String)

    suspend fun deleteLobby(gameId: String)

    suspend fun getGamesList(): Flow<List<Game>>

}