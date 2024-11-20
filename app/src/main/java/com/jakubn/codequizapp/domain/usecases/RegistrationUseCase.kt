package com.jakubn.codequizapp.domain.usecases

import com.jakubn.codequizapp.domain.repository.UserAuthRepository
import javax.inject.Inject


data class RegistrationUseCase @Inject constructor(
    val signUpUser: SignUpUser
) {

    class SignUpUser @Inject constructor(private val repository: UserAuthRepository) {
        suspend operator fun invoke(email: String, password: String) =
            repository.signUpUser(email, password)
    }
}
