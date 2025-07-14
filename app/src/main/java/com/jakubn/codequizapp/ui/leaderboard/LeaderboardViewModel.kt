package com.jakubn.codequizapp.ui.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.data.repositoryImpl.UserDataRepository
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
) : ViewModel() {

    private val _leaderboardUsers = MutableStateFlow<CustomState<List<User>>>(CustomState.Idle)
    val leaderboardUsers: StateFlow<CustomState<List<User>>> = _leaderboardUsers

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
                .catch { message ->
                    _leaderboardUsers.value = CustomState.Failure(message.message)
                }
                .collect { games ->
                    _leaderboardUsers.value = CustomState.Success(games)
                }
        }

    }
}