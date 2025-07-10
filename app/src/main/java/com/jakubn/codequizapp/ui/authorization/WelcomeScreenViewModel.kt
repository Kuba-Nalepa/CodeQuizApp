package com.jakubn.codequizapp.ui.authorization

import androidx.lifecycle.ViewModel
import com.jakubn.codequizapp.data.repositoryImpl.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WelcomeScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    fun getCurrentUser() = authRepository.getCurrentUser()

}