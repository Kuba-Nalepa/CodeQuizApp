package com.jakubn.codequizapp.domain.usecases

import com.jakubn.codequizapp.data.repositoryImpl.AuthRepositoryImpl
import javax.inject.Inject

data class GetCurrentUserUseCase @Inject constructor(
    val getCurrentUser: GetCurrentUser
) {
    class GetCurrentUser @Inject constructor(private val repository: AuthRepositoryImpl) {
        operator fun invoke() = repository.getCurrentUser()
    }
}