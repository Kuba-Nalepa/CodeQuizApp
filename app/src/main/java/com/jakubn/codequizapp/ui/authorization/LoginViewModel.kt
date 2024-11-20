package com.jakubn.codequizapp.ui.authorization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.jakubn.codequizapp.domain.usecases.LoginUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val authUseCases: LoginUseCase
) : ViewModel() {

    fun signIn(email: String, password: String, onResult: (FirebaseUser?) -> Unit) {
        viewModelScope.launch {
            val user = authUseCases.signInUser(email, password)
            onResult(user)
        }
    }

    fun signOut() {
        authUseCases.signOutUser()
    }

    fun getCurrentUser(): FirebaseUser? {
        return authUseCases.getCurrentUser()
    }

}
