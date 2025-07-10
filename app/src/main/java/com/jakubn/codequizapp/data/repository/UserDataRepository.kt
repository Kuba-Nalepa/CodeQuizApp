package com.jakubn.codequizapp.data.repository

import android.net.Uri
import com.jakubn.codequizapp.model.User
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

    suspend fun getUserData(): Flow<User>

    suspend fun updateUserProfile(updatedUser: User): Flow<Unit>

    suspend fun updateUserData(user: User, score: Int, hasUserWon: Boolean)

    suspend fun uploadProfileImage(imageUri: Uri): String

}