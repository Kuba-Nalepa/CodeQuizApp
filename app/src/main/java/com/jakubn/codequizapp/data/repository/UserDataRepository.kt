package com.jakubn.codequizapp.data.repository

import android.net.Uri
import com.jakubn.codequizapp.model.Friend
import com.jakubn.codequizapp.model.FriendshipRequest
import com.jakubn.codequizapp.model.GameRequest
import com.jakubn.codequizapp.model.Message
import com.jakubn.codequizapp.model.User
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

    suspend fun getUserData(): Flow<User>

    suspend fun updateUserProfile(updatedUser: User): Flow<Unit>

    suspend fun updateUserData(user: User, score: Int, hasUserWon: Boolean)

    suspend fun uploadProfileImage(imageUri: Uri): String

    suspend fun getUsersFriends(userId: String): List<Friend>

    suspend fun observeUserData(): Flow<User?>

    suspend fun sendFriendshipRequest(senderId: String, receiverId: String, senderName: String)

    suspend fun acceptFriendshipRequest(friendshipId: String)

    suspend fun declineFriendshipRequest(friendshipId: String)

    fun observeFriendsList(userId: String): Flow<List<Friend>>

    fun observeUsersInRanking(): Flow<List<User>>

    fun observeFriendshipRequestStatus(myUserId: String, otherUserId: String): Flow<FriendshipRequest?>

    fun observeFriendshipRequests(userId: String): Flow<List<FriendshipRequest>>

    fun observeChatMessages(chatId: String): Flow<List<Message>>

    fun observeFriendInvitationsCount(userId: String): Flow<Long>

    fun observeGameInvitationsCount(userId: String): Flow<Long>

    fun observeGameRequest(userId: String): Flow<List<GameRequest>>

    suspend fun createChat(userUid: String,friendUid: String): String

    suspend fun sendMessage(chatId: String, userUid: String, messageText: String)

    suspend fun checkChatExists(myUserId: String, friendUid: String): Boolean

    suspend fun decrementFriendInvitationCount(userId: String)

    suspend fun decrementGameInvitationCount(userId: String)
}