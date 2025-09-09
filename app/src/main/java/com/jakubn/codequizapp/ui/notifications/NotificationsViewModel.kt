package com.jakubn.codequizapp.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.data.repositoryImpl.GameRepository
import com.jakubn.codequizapp.data.repositoryImpl.UserDataRepository
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.model.Notification
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
class NotificationsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _notificationsState =
        MutableStateFlow<CustomState<List<Notification>>>(CustomState.Idle)
    val notificationsState: StateFlow<CustomState<List<Notification>>> =
        _notificationsState.asStateFlow()
    private var currentUserId: String? = null

    fun fetchNotifications(userId: String) {
        currentUserId = userId
        viewModelScope.launch {
            combine(
                userDataRepository.observeFriendshipRequests(userId),
                userDataRepository.observeGameRequest(userId)
            ) { friendshipRequests, gameRequests ->
                val friendNotification = friendshipRequests.map { Notification.FriendInvite(it) }
                val gameNotification = gameRequests.map { Notification.GameInvite(it) }
                friendNotification + gameNotification
            }
                .onStart { _notificationsState.value = CustomState.Loading }
                .catch { e -> _notificationsState.value = CustomState.Failure(e.message) }
                .collect { combinedNotifications ->
                    _notificationsState.value = CustomState.Success(combinedNotifications)
                }
        }
    }

    fun handleFriendshipAccept(friendshipId: String) {
        viewModelScope.launch {
            userDataRepository.acceptFriendshipRequest(friendshipId)
            currentUserId?.let { userId ->
                userDataRepository.decrementFriendInvitationCount(userId)
            }
        }
    }

    fun handleFriendshipDecline(friendshipId: String) {
        viewModelScope.launch {
            userDataRepository.declineFriendshipRequest(friendshipId)
            currentUserId?.let { userId ->
                userDataRepository.decrementFriendInvitationCount(userId)
            }
        }
    }

    fun handleGameInvitationAccept(gameId: String) {
        viewModelScope.launch {
            gameRepository.acceptGameInvite(gameId)
            currentUserId?.let { userId ->
                userDataRepository.decrementGameInvitationCount(userId)
            }
        }
    }

    fun handleGameInvitationDecline(gameId: String) {
        viewModelScope.launch {
            gameRepository.deleteGameInvitation(gameId)
            currentUserId?.let { userId ->
                userDataRepository.decrementGameInvitationCount(userId)
            }
        }
    }
}
