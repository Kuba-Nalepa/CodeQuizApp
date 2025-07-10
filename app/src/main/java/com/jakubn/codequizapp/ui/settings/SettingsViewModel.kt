package com.jakubn.codequizapp.ui.settings


import androidx.lifecycle.ViewModel
import com.jakubn.codequizapp.data.repositoryImpl.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun signOut() = authRepository.signOutUser()
}