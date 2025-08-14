package com.jakubn.codequizapp.ui.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.data.repositoryImpl.UserDataRepository
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.model.Friend
import com.jakubn.codequizapp.model.FriendshipRequest
import com.jakubn.codequizapp.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
) : ViewModel() {

    private val _leaderboardUsers = MutableStateFlow<CustomState<List<User>>>(CustomState.Idle)
    val leaderboardUsers: StateFlow<CustomState<List<User>>> = _leaderboardUsers

    private val _friendshipStatus = MutableStateFlow<CustomState<FriendshipRequest?>>(CustomState.Idle)
    val friendshipStatus: StateFlow<CustomState<FriendshipRequest?>> = _friendshipStatus

    private val _friendsList = MutableStateFlow<List<Friend>>(emptyList())
    val friendsList: StateFlow<List<Friend>> = _friendsList.asStateFlow()

    private val _uiEvents = Channel<String>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()


    init {
        fetchLeaderboard()
    }

    private fun fetchLeaderboard() {
        _leaderboardUsers.value = CustomState.Loading

        viewModelScope.launch {
            userDataRepository.getUsers()
                .onStart {
                    _leaderboardUsers.value = CustomState.Loading
                }
                .map { users ->
                    users.sortedByDescending { it.score }
                }
                .catch { error ->
                    _leaderboardUsers.value = CustomState.Failure(error.message)
                }
                .collect { games ->
                    _leaderboardUsers.value = CustomState.Success(games)
                }
        }

    }

    fun sendFriendshipRequest(senderId: String, receiverId: String) {
        viewModelScope.launch {
            try {
                userDataRepository.sendFriendshipRequest(senderId, receiverId)
                _uiEvents.send("friend_request_sent")
            } catch (e: Exception) {
                _uiEvents.send("error")
            }
        }
    }

    fun fetchMyFriends(userId: String) {
        viewModelScope.launch {
            userDataRepository.observeFriendsList(userId).collect { friends ->
                _friendsList.value = friends
            }
        }
    }

    fun startListeningForFriendshipStatus(myUserId: String, otherUserId: String) {
        viewModelScope.launch {
            userDataRepository.observeFriendshipRequestStatus(myUserId, otherUserId)
                .onStart {
                    _friendshipStatus.value = CustomState.Loading
                }
                .catch { error ->
                    _friendshipStatus.value = CustomState.Failure(error.message)
                }
                .collect { friendshipRequest ->
                    _friendshipStatus.value = CustomState.Success(friendshipRequest)
                }
        }
    }
}