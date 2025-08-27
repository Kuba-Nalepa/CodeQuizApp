package com.jakubn.codequizapp.model

data class FriendshipRequest(
    val id: String? = null,
    val senderId: String? = null,
    val senderName: String? = null,
    val senderImageUri: String? = null,
    val receiverId: String? = null,
    val status: String? = null
)
