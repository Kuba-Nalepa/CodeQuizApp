package com.jakubn.codequizapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.data.repositoryImpl.UserDataRepository
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.model.Friend
import com.jakubn.codequizapp.model.FriendshipRequest
import com.jakubn.codequizapp.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
) : ViewModel() {

    private val _state = MutableStateFlow<CustomState<User?>>(CustomState.Idle)
    val state: StateFlow<CustomState<User?>> = _state.asStateFlow()

    private val _friendsState = MutableStateFlow<CustomState<List<Friend>>>(CustomState.Idle)
    val friendsState: StateFlow<CustomState<List<Friend>>> = _friendsState.asStateFlow()

    private val _requestsState =
        MutableStateFlow<CustomState<List<FriendshipRequest>>>(CustomState.Idle)
    val requestsState: StateFlow<CustomState<List<FriendshipRequest>>> =
        _requestsState.asStateFlow()

    private val _notificationState = MutableStateFlow<CustomState<Int>>(CustomState.Idle)
    val notificationState: StateFlow<CustomState<Int>> = _notificationState.asStateFlow()


    init {
        observeUserDataAndNotifications()
    }

    private fun observeUserDataAndNotifications() {
        viewModelScope.launch {
            userDataRepository.observeUserData()
                .onStart {
                    _state.value = CustomState.Loading
                    _friendsState.value = CustomState.Loading
                    _requestsState.value = CustomState.Loading
                }
                .catch { throwable ->
                    _state.value = CustomState.Failure(throwable.message)
                    _friendsState.value = CustomState.Failure(throwable.message)
                    _requestsState.value = CustomState.Failure(throwable.message)
                }
                .collect { user ->
                    _state.value = CustomState.Success(user)
                    if (user?.uid != null) {
                        observeNotifications(user.uid)
                        observeFriends(user.uid)
                        observePendingRequests(user.uid)
                    } else {
                        _friendsState.value = CustomState.Success(emptyList())
                        _requestsState.value = CustomState.Success(emptyList())
                    }
                }
        }
    }

    private fun observeNotifications(userId: String) {
        viewModelScope.launch {
            combine(
                userDataRepository.observeFriendInvitationsCount(userId),
                userDataRepository.observeGameInvitationsCount(userId)
            ) { friendCount, gameCount ->
                friendCount.toInt() + gameCount.toInt()
            }
                .onStart { _notificationState.value = CustomState.Loading }
                .catch { throwable ->
                    _notificationState.value = CustomState.Failure(throwable.message)
                }
                .collect { totalCount ->
                    _notificationState.value = CustomState.Success(totalCount)
                }
        }
    }

    private fun observeFriends(userId: String) {
        viewModelScope.launch {
            userDataRepository.observeFriendsList(userId)
                .onStart { _friendsState.value = CustomState.Loading }
                .catch { throwable -> _friendsState.value = CustomState.Failure(throwable.message) }
                .collect { friendsList ->
                    _friendsState.value = CustomState.Success(friendsList)
                }
        }
    }

    private fun observePendingRequests(userId: String) {
        viewModelScope.launch {
            userDataRepository.observePendingFriendsRequestsWithSenderData(userId)
                .onStart { _requestsState.value = CustomState.Loading }
                .catch { throwable ->
                    _requestsState.value = CustomState.Failure(throwable.message)
                }
                .collect { requestsList ->
                    _requestsState.value = CustomState.Success(requestsList)
                }
        }
    }

    fun acceptFriendRequest(friendshipId: String) {
        viewModelScope.launch {
            val userId = (_state.value as? CustomState.Success)?.result?.uid ?: return@launch
            userDataRepository.acceptFriendshipRequest(friendshipId)
            userDataRepository.decrementFriendInvitationCount(userId)

        }
    }

    fun declineFriendRequest(friendshipId: String) {
        viewModelScope.launch {
            val userId = (_state.value as? CustomState.Success)?.result?.uid ?: return@launch
            userDataRepository.declineFriendshipRequest(friendshipId)
            userDataRepository.decrementFriendInvitationCount(userId)
        }
    }

    fun resetFriendInvitationCount() {
        viewModelScope.launch {
            val userId = (_state.value as? CustomState.Success)?.result?.uid ?: return@launch
            userDataRepository.resetFriendInvitationCount(userId)
        }
    }

    fun resetGameInvitationCount() {
        viewModelScope.launch {
            val userId = (_state.value as? CustomState.Success)?.result?.uid ?: return@launch
            userDataRepository.resetGameInvitationCount(userId)
        }
    }
}