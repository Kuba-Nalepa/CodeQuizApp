package com.jakubn.codequizapp.ui.authorization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.domain.model.User
import com.jakubn.codequizapp.domain.usecases.user.RegistrationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val registrationUseCase: RegistrationUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<CustomState<User>>(CustomState.Idle)
    val authState: StateFlow<CustomState<User>> = _authState

    fun signUpUser(name: String, email: String, password: String) {
        viewModelScope.launch {
            registrationUseCase.signUpUser(name, email, password)
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

    fun resetState() {
        _authState.value = CustomState.Idle
    }
}