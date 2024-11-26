package com.jakubn.codequizapp.domain.repository

import com.jakubn.codequizapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun signUpUser(name: String, email:String, password: String): Flow<User>

    suspend fun signInUser(email:String, password: String): Flow<User>

    fun signOutUser(): Boolean

}