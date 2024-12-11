package com.jakubn.codequizapp.domain.repository

import kotlinx.coroutines.flow.Flow


interface GameRepository {

    suspend fun createGame(questionCategory: String, questionQuantity: Int, questionDuration: Int): Flow<Boolean>

}