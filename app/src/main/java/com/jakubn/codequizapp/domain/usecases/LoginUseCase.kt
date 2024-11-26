package com.jakubn.codequizapp.domain.usecases

import com.jakubn.codequizapp.domain.repositoryImpl.UserAuthRepositoryImpl
import javax.inject.Inject

data class LoginUseCase @Inject constructor(
    val signInUser: SignInUser,
    val signOutUser: SignOutUser
) {
    class SignInUser @Inject constructor(private val repository: UserAuthRepositoryImpl) {
        suspend operator fun invoke(email: String, password: String) =
             repository.signInUser(email, password)
    }

    class SignOutUser @Inject constructor(private val repository: UserAuthRepositoryImpl) {
        operator fun  invoke() = repository.signOutUser()
    }
}