package com.jakubn.codequizapp.data

import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.domain.model.User
import com.jakubn.codequizapp.domain.usecases.user.GetUserDataUseCase
import com.jakubn.codequizapp.domain.usecases.user.SignOutUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserManager @Inject constructor(
    private val getUserDataUseCase: GetUserDataUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val applicationScope: CoroutineScope
) {
    private val _userState = MutableStateFlow<CustomState<User>>(CustomState.Idle)
    val userState: StateFlow<CustomState<User>> = _userState.asStateFlow()

    init {
        applicationScope.launch {
            loadUserData()
        }
    }

    private suspend fun loadUserData() {
        getUserDataUseCase.getUserData()
            .onStart {
                _userState.value = CustomState.Loading
            }
            .catch { throwable ->
                _userState.value = CustomState.Failure(throwable.message ?: "Failed to load user")
            }
            .collect { user ->
                _userState.value = CustomState.Success(user)
            }
    }

    fun onUserAuthenticated(user: User) {
        _userState.value = CustomState.Success(user)
    }

    fun onUserLoggedOut() {
        applicationScope.launch {
            _userState.value = CustomState.Loading
            try {
                signOutUseCase.signOutUser.invoke()
                _userState.value = CustomState.Failure("User logged out successfully")
            } catch (e: Exception) {
                _userState.value = CustomState.Failure("Logout failed: ${e.message}")
            }
        }
    }
}