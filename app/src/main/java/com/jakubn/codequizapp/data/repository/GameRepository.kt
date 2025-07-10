package com.jakubn.codequizapp.data.repository

import com.jakubn.codequizapp.model.Game
import com.jakubn.codequizapp.model.Lobby
import com.jakubn.codequizapp.model.User
import kotlinx.coroutines.flow.Flow


interface GameRepository {

    suspend fun createGame(questionCategory: String, questionQuantity: Int, questionDuration: Int, founder: User): Flow<String>

    suspend fun manageGameState(gameId: String, state: Boolean)

    suspend fun listenGameDataChanges(gameId: String): Flow<Game>

    suspend fun getGameData(gameId: String): Flow<Game>

    suspend fun addMemberToLobby(gameId: String, user: User)

    suspend fun removeMemberFromLobby(gameId: String)

    suspend fun deleteLobby(gameId: String)

    suspend fun changeUserReadinessStatus(gameId: String, lobby: Lobby, user: User, userReadinessStatus: Boolean)

    suspend fun getGamesList(): Flow<List<Game>>

    suspend fun saveUserGameStats(gameId: String, lobby: Lobby, user: User, answersList: List<Int>, correctAnswersQuantity: Int, points: Int)

    suspend fun setUserFinishedGame(gameId: String, lobby: Lobby, user: User, hasFinished: Boolean)

    suspend fun setUserLeftGame(game: Game, user: User, hasLeft: Boolean)

    suspend fun archiveGame(game: Game)

    suspend fun deleteGame(game: Game)

}