package com.jakubn.codequizapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.data.UserManager
import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userManager: UserManager
) : ViewModel() {

    val currentUser: StateFlow<CustomState<User>> = userManager.userState

    init {
        // Any other ViewModel-specific startup logic can go here.
        // Initial user loading is handled by UserManager's init block.
    }

    /**
     * Delegates the logout action to the UserManager.
     * UserManager will handle the actual sign-out logic and update its internal userState.
     */
    fun logout() {
        viewModelScope.launch {
            userManager.onUserLoggedOut()
        }
    }

    /**
     * Delegates the successful login/registration event to the UserManager.
     * UserManager will update its internal userState with the newly authenticated user.
     */
    fun onLoginSuccess(loggedInUser: User) {
        viewModelScope.launch {
            userManager.onUserAuthenticated(loggedInUser)
        }
    }
}