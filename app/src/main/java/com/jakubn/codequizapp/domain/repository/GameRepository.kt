package com.jakubn.codequizapp.domain.repository

import com.jakubn.codequizapp.domain.model.Game
import com.jakubn.codequizapp.domain.model.Lobby
import kotlinx.coroutines.flow.Flow


interface GameRepository {

    suspend fun createGame(questionCategory: String, questionQuantity: Int, questionDuration: Int): Flow<String>

    suspend fun getLobbyData(gameId: String): Flow<Lobby>

    suspend fun getGamesList(): Flow<List<Game>>

}