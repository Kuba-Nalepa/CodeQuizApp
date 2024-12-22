package com.jakubn.codequizapp.data.repository

import com.google.firebase.auth.FirebaseUser
import com.jakubn.codequizapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun signUpUser(name: String, email:String, password: String): Flow<User>

    suspend fun signInUser(email:String, password: String): Flow<User>

    fun getCurrentUser(): FirebaseUser?

    fun signOutUser()

}