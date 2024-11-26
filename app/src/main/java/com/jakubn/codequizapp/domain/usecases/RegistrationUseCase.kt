package com.jakubn.codequizapp.domain.usecases

import com.jakubn.codequizapp.domain.repositoryImpl.UserAuthRepositoryImpl
import javax.inject.Inject


data class RegistrationUseCase @Inject constructor(
    val signUpUser: SignUpUser
) {

    class SignUpUser @Inject constructor(private val repository: UserAuthRepositoryImpl) {
        suspend operator fun invoke(name: String, email: String, password: String) = repository.signUpUser(name, email, password)
    }
}
