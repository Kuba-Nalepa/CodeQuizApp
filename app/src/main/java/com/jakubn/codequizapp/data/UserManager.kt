package com.jakubn.codequizapp.data

import com.jakubn.codequizapp.data.repositoryImpl.AuthRepository
import com.jakubn.codequizapp.data.repositoryImpl.UserDataRepository
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.model.User
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

    private val userDataRepository: UserDataRepository,
    private val authRepository: AuthRepository,
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
        userDataRepository.getUserData()
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
                authRepository.signOutUser()
                _userState.value = CustomState.Failure("User logged out successfully")
            } catch (e: Exception) {
                _userState.value = CustomState.Failure("Logout failed: ${e.message}")
            }
        }
    }
}