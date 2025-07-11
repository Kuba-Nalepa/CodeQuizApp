package com.jakubn.codequizapp.ui.authorization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.data.repositoryImpl.AuthRepository
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<CustomState<User>>(CustomState.Idle)
    val authState: StateFlow<CustomState<User>> = _authState

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    private val maxLengthName = 13

    fun onNameChange(newName: String) {
        val currentNameLength = _name.value.length
        if (newName.length <= maxLengthName) {
            _name.value = newName
        } else {
            if (currentNameLength == maxLengthName) {
                viewModelScope.launch {
                    _toastEvent.emit("Name cannot exceed $maxLengthName characters")
                }
            }
            _name.value = newName.take(maxLengthName)
        }
    }

    val isSignUpButtonEnabled: StateFlow<Boolean> = combine(
        _name,
        _email,
        _password,
        _authState
    ) { name, email, password, authState ->
        val isNameValid = name.length in 1..maxLengthName
        val isEmailValid = email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid = password.length >= 6

        isNameValid && isEmailValid && isPasswordValid && authState !is CustomState.Loading
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun signUpUser() {
        viewModelScope.launch {
            authRepository.signUpUser(
                name = _name.value,
                email = _email.value,
                password = _password.value
            )
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

    fun signOut() {
        authRepository.signOutUser()
    }

    fun resetState() {
        _authState.value = CustomState.Idle
        _name.value = ""
        _email.value = ""
        _password.value = ""
    }
}