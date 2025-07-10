package com.jakubn.codequizapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.data.repositoryImpl.UserDataRepository
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
) :
    ViewModel() {

    private val _state = MutableStateFlow<CustomState<User>>(CustomState.Idle)
    val state: StateFlow<CustomState<User>> = _state

    init {
        getUserData()
    }

    private fun getUserData() {
        viewModelScope.launch {
            userDataRepository.getUserData()
                .onStart {
                    _state.value = CustomState.Loading
                }
                .catch { throwable ->
                    _state.value = CustomState.Failure(throwable.message)
                }
                .collect { user ->
                    _state.value = CustomState.Success(user)

                }
        }
    }
}