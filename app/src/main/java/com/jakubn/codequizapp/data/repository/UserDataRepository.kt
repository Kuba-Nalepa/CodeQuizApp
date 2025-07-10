package com.jakubn.codequizapp.data.repository // Your repository interface package

import android.net.Uri
import com.jakubn.codequizapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

    suspend fun getUserData(): Flow<User>

    suspend fun updateUserProfile(updatedUser: User)

    suspend fun updateUserData(user: User, score: Int, hasUserWon: Boolean)

    suspend fun uploadProfileImage(imageUri: Uri): String

}