package com.jakubn.codequizapp.data

import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.domain.model.User
import com.jakubn.codequizapp.domain.usecases.user.GetUserDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserManager @Inject constructor(
    private val getUserDataUseCase: GetUserDataUseCase
) {
    private val _userState = MutableStateFlow<CustomState<User>>(CustomState.Idle)
    val userState: StateFlow<CustomState<User>> = _userState

    suspend fun loadUserData() {
        getUserDataUseCase.getUserData()
            .onStart {
                _userState.value = CustomState.Loading
            }
            .catch { throwable ->
                _userState.value = CustomState.Failure(throwable.message)
            }
            .collect { user ->
                _userState.value = CustomState.Success(user)
            }
    }
}