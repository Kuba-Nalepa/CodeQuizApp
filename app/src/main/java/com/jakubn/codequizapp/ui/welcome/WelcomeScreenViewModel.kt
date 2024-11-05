package com.jakubn.codequizapp.ui.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.jakubn.codequizapp.data.AuthRepository
import com.jakubn.codequizapp.data.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class WelcomeScreenViewModel @Inject constructor (
    private val repository: AuthRepository
): ViewModel() {

    private val _loginFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val loginFlow: StateFlow<Resource<FirebaseUser>?> = _loginFlow

    private val _signupFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val signupFlow: StateFlow<Resource<FirebaseUser>?> = _loginFlow

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading

    val currentUser: FirebaseUser?
        get() = repository.currentUser

    init {
        _isLoading.value = true

        if(repository.currentUser != null) {
            _loginFlow.value = Resource.Success(repository.currentUser!!)
        }
        _isLoading.value = false

    }

    fun login(email: String, password: String) = viewModelScope.launch {
        _isLoading.value = true

        _loginFlow.value = Resource.Loading
        val result = repository.login(email, password)
        _loginFlow.value = result

        _isLoading.value = false
    }

    fun signup(name: String, email: String, password: String) = viewModelScope.launch {
        _isLoading.value = true

        _signupFlow.value = Resource.Loading
        val result = repository.signUp(name, email, password)
        _signupFlow.value = result

        _isLoading.value = false

    }


}
