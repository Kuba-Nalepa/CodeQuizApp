package com.jakubn.codequizapp.domain.usecases

import com.jakubn.codequizapp.data.repositoryImpl.UserDataRepositoryImpl
import javax.inject.Inject

data class GetUserDataUseCase @Inject constructor(
   val getUserData: GetUserData
) {
    class GetUserData @Inject constructor(private val repository: UserDataRepositoryImpl) {
        suspend operator fun invoke() = repository.getUserData()
    }
}