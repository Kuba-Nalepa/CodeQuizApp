package com.jakubn.codequizapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.data.UserManager
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userManager: UserManager
) : ViewModel() {

    val currentUser: StateFlow<CustomState<User>> = userManager.userState

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun logout() {
        viewModelScope.launch {
            userManager.onUserLoggedOut()
        }
    }

    fun onLoginSuccess(loggedInUser: User) {
        viewModelScope.launch {
            userManager.onUserAuthenticated(loggedInUser)
        }
    }

    fun navigateTo(route: String) {
        viewModelScope.launch {
            _navigationEvent.emit(route)
        }
    }
}