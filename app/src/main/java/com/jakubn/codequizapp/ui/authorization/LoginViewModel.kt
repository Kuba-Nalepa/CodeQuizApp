package com.jakubn.codequizapp.ui.authorization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.jakubn.codequizapp.domain.usecases.LoginUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val logInUseCase: LoginUseCase
) : ViewModel() {

}
