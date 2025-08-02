package com.jakubn.codequizapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.data.repositoryImpl.UserDataRepository
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.model.Friend
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

    private val _state = MutableStateFlow<CustomState<User?>>(CustomState.Idle)
    val state: StateFlow<CustomState<User?>> = _state

    private val _friendsState = MutableStateFlow<CustomState<List<Friend>>>(CustomState.Idle)
    val friendsState: StateFlow<CustomState<List<Friend>>> = _friendsState

    init {
        observeUserData()
    }

    private fun observeUserData() {
        viewModelScope.launch {
            userDataRepository.observeUserData()
                .onStart {
                    _state.value = CustomState.Loading
                    _friendsState.value = CustomState.Loading
                }
                .catch { throwable ->
                    _state.value = CustomState.Failure(throwable.message)
                    _friendsState.value = CustomState.Failure(throwable.message)

                }
                .collect { user ->
                    _state.value = CustomState.Success(user)
                    if (user?.uid != null) {
                        observeFriends(user.uid)
                    } else {
                        _friendsState.value = CustomState.Success(emptyList())
                    }

                }
        }
    }

    private fun observeFriends(userId: String) {
        viewModelScope.launch {
            userDataRepository.listenForFriendsList(userId)
                .onStart {
                    _friendsState.value = CustomState.Loading
                }
                .catch { throwable ->
                    _friendsState.value = CustomState.Failure(throwable.message)
                }
                .collect { friendsList ->
                    _friendsState.value = CustomState.Success(friendsList)
                }
        }
    }
}