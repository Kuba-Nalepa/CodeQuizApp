package com.jakubn.codequizapp.domain.usecases

import com.jakubn.codequizapp.data.repositoryImpl.AuthRepositoryImpl
import javax.inject.Inject


data class SignOutUseCase @Inject constructor(
    val signOutUser: SignOutUser
) {
    class SignOutUser @Inject constructor(private val repository: AuthRepositoryImpl) {
        operator fun invoke() = repository.signOutUser()
    }
}
