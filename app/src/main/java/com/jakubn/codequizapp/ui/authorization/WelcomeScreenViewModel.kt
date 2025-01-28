package com.jakubn.codequizapp.ui.authorization

import androidx.lifecycle.ViewModel
import com.jakubn.codequizapp.domain.usecases.user.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WelcomeScreenViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
): ViewModel() {

    fun getCurrentUser() = getCurrentUserUseCase.getCurrentUser.invoke()

}