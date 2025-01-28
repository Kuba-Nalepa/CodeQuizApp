package com.jakubn.codequizapp.domain.usecases.user

import com.jakubn.codequizapp.data.repositoryImpl.AuthRepositoryImpl
import javax.inject.Inject


data class RegistrationUseCase @Inject constructor(
    val signUpUser: SignUpUser
) {

    class SignUpUser @Inject constructor(private val repository: AuthRepositoryImpl) {
        suspend operator fun invoke(name: String, email: String, password: String) = repository.signUpUser(name, email, password)
    }
}
