package com.jakubn.codequizapp.domain.usecases

import com.jakubn.codequizapp.domain.repository.UserAuthRepository
import javax.inject.Inject

data class LoginUseCase @Inject constructor(
    val signInUser: SignInUser,
    val signOutUser: SignOutUser,
    val getCurrentUser: GetCurrentUser
) {
    class SignInUser @Inject constructor(private val repository: UserAuthRepository) {
        suspend operator fun invoke(email: String, password: String) =
            repository.signInUser(email, password)
    }

    class SignOutUser @Inject constructor(private val repository: UserAuthRepository) {
        operator fun invoke() = repository.signOutUser()
    }

    class GetCurrentUser @Inject constructor(private val repository: UserAuthRepository) {
        operator fun invoke() = repository.getCurrentUser()
    }
}