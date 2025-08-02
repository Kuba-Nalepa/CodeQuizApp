package com.jakubn.codequizapp.data.repository

import android.net.Uri
import com.jakubn.codequizapp.model.Friend
import com.jakubn.codequizapp.model.FriendshipRequest
import com.jakubn.codequizapp.model.User
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

    suspend fun getUserData(): Flow<User>

    suspend fun updateUserProfile(updatedUser: User): Flow<Unit>

    suspend fun updateUserData(user: User, score: Int, hasUserWon: Boolean)

    suspend fun uploadProfileImage(imageUri: Uri): String

    suspend fun getUsers(): Flow<List<User>>

    suspend fun observeUserData(): Flow<User?>

    suspend fun sendFriendshipRequest(senderId: String, receiverId: String)

    suspend fun acceptFriendshipRequest(friendshipId: String)

    suspend fun deleteFriend(friendshipId: String, friendId: String)

    suspend fun observePendingFriendsRequest(): Flow<List<FriendshipRequest>>

    suspend fun listenForFriendsList(userId: String): Flow<List<Friend>>

    fun listenForFriendshipRequestStatus(myUserId: String, otherUserId: String): Flow<FriendshipRequest?>

}