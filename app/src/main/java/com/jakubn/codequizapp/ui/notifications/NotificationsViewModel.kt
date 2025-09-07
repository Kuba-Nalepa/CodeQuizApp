package com.jakubn.codequizapp.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val userDataRepository: UserDataRepository
) : ViewModel() {

    private val _notificationsState =
        MutableStateFlow<CustomState<List<Notification>>>(CustomState.Idle)
    val notificationsState: StateFlow<CustomState<List<Notification>>> =
        _notificationsState.asStateFlow()

    fun fetchNotifications(userId: String) {
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
}
