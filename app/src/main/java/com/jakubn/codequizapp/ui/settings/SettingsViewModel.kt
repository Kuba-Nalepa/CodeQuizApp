package com.jakubn.codequizapp.ui.settings


import androidx.lifecycle.ViewModel
import com.jakubn.codequizapp.domain.usecases.user.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    fun signOut() = signOutUseCase.signOutUser.invoke()
}