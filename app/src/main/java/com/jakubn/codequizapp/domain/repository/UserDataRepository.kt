package com.jakubn.codequizapp.domain.repository

import com.jakubn.codequizapp.domain.model.User
import kotlinx.coroutines.flow.Flow


interface UserDataRepository {

    suspend fun getUserData(): Flow<User>


}