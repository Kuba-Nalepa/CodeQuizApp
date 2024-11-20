package com.jakubn.codequizapp.ui.authorization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.jakubn.codequizapp.domain.usecases.RegistrationUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegistrationViewModel @Inject constructor(
    private val registrationUseCases: RegistrationUseCase
) : ViewModel() {

    fun signUp(email: String, password: String, onResult: (FirebaseUser?) -> Unit) {
        viewModelScope.launch {
            val user = registrationUseCases.signUpUser(email, password)
            onResult(user)
        }
    }
}