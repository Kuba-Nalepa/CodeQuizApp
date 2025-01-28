package com.jakubn.codequizapp.data.repository

import com.jakubn.codequizapp.domain.model.User
import kotlinx.coroutines.flow.Flow


interface UserDataRepository {

    suspend fun getUserData(): Flow<User>

    suspend fun updateUserData(score: Int, hasUserWon: Boolean)


}