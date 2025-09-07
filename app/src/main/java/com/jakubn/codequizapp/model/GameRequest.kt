package com.jakubn.codequizapp.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class GameRequest(
    val gameId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val senderName: String = "",
    val senderImageUri: String? = null,
    val category: String? = null,
    val questionDuration: Int? = null,
    @ServerTimestamp val timestamp: Date? = null,
    val status: String = "pending"
)