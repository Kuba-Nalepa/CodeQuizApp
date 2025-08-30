package com.jakubn.codequizapp.ui.authorization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.data.repositoryImpl.AuthRepository
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<CustomState<User>>(CustomState.Idle)
    val authState: StateFlow<CustomState<User>> = _authState

    fun signInUser(email: String, password: String) {
        viewModelScope.launch {
            authRepository.signInUser(email, password)
                .onStart {
                    _authState.value = CustomState.Loading
                }
                .catch { throwable ->
                    _authState.value = CustomState.Failure(throwable.message)
                }
                .collect { user ->
                    _authState.value = CustomState.Success(user)
                }
        }
    }

    fun saveFCMToken(userId: String) {
        viewModelScope.launch {
            authRepository.saveFCMTokenToFirestore(userId)

        }
    }

    fun resetState() {
        _authState.value = CustomState.Idle
    }

}
