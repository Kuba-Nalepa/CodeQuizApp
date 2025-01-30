package com.jakubn.codequizapp.domain.usecases.user

import com.jakubn.codequizapp.data.repositoryImpl.UserDataRepositoryImpl
import com.jakubn.codequizapp.domain.model.User
import javax.inject.Inject

data class UpdateUserDataUsecase @Inject constructor(
    val updateUserData: UpdateUserData
) {
    class UpdateUserData @Inject constructor(private val repository: UserDataRepositoryImpl) {
        suspend operator fun invoke(
            user: User,
            score: Int,
            hasUserWon: Boolean
        ) = repository.updateUserData(user, score, hasUserWon)
    }
}

