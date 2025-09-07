package com.jakubn.codequizapp.model

sealed class Notification {
    data class FriendInvite(val request: FriendshipRequest) : Notification()
    data class GameInvite(val request: GameRequest) : Notification()
}