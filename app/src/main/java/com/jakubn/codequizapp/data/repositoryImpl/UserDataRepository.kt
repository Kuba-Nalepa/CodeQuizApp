package com.jakubn.codequizapp.data.repositoryImpl

import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.functions.functions
import com.google.firebase.storage.FirebaseStorage
import com.jakubn.codequizapp.model.User
import com.jakubn.codequizapp.data.repository.UserDataRepository
import com.jakubn.codequizapp.model.Friend
import com.jakubn.codequizapp.model.FriendshipRequest
import com.jakubn.codequizapp.model.GameRequest
import com.jakubn.codequizapp.model.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
) : UserDataRepository {

    override suspend fun getUserData(): Flow<User> {
        return flow {
            val currentUserUid =
                firebaseAuth.currentUser?.uid ?: throw Exception("User uid does not exist")
            val user = firebaseFirestore.collection("users").document(currentUserUid).get().await()
                .toObject(
                    User::class.java
                ) ?: throw Exception("Data fetching failed")

            emit(user)
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun updateUserData(user: User, score: Int, hasUserWon: Boolean) {
        withContext(Dispatchers.IO) {

            val userId = user.uid ?: throw Exception("User not authenticated")
            val userDocRef = firebaseFirestore.collection("users").document(userId)

            try {
                val document = userDocRef.get().await()
                if (!document.exists()) throw Exception("User document not found")

                val currentWins = document.getLong("wins") ?: 0L
                val currentGamesPlayed = document.getLong("gamesPlayed") ?: 0L

                val winsIncrement = if (hasUserWon) 1L else 0L
                val updatedWins = currentWins + winsIncrement
                val updatedGamesPlayed = currentGamesPlayed + 1L

                val winRatio = when {
                    updatedGamesPlayed == 0L -> 0.000
                    else -> BigDecimal(updatedWins.toDouble() / updatedGamesPlayed.toDouble())
                        .setScale(3, RoundingMode.HALF_UP)
                        .toDouble()
                }

                val updates = mapOf(
                    "score" to FieldValue.increment(score.toLong()),
                    "gamesPlayed" to FieldValue.increment(1L),
                    "wins" to FieldValue.increment(winsIncrement),
                    "winRatio" to winRatio
                )

                userDocRef.update(updates).await()

            } catch (e: Exception) {
                throw Exception("Failed to update user data: ${e.message}")
            }
        }
    }

    override suspend fun uploadProfileImage(imageUri: Uri): String {
        val userId = firebaseAuth.currentUser?.uid
            ?: throw Exception("User not authenticated. Cannot upload image.")

        return try {
            val storageRef = firebaseStorage.reference
            val fileName = "avatars/${UUID.randomUUID()}.jpg"
            val imageRef = storageRef.child("users/$userId/$fileName")

            val uploadTask = imageRef.putFile(imageUri)
            uploadTask.await()

            val downloadUrl = imageRef.downloadUrl.await()
            downloadUrl.toString()
        } catch (e: Exception) {
            throw Exception("Failed to upload image to Firebase Storage: ${e.localizedMessage}", e)
        }
    }

    override fun observeUsersInRanking(): Flow<List<User>> = callbackFlow {
        val userDocRef = firebaseFirestore.collection("users")

        val subscription =
            userDocRef.addSnapshotListener { snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val users = snapshot.documents.map { document ->
                        document.toObject(User::class.java)?.copy(uid = document.id)
                            ?: User(uid = document.id)
                    }
                    trySend(users).isSuccess
                } else {
                    trySend(emptyList()).isSuccess
                }
            }

        awaitClose {
            subscription.remove()
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getUsersFriends(userId: String): List<Friend> {
        return firebaseFirestore.collection("users").document(userId).collection("friends").get()
            .await()
            .toObjects(Friend::class.java)

    }

    override suspend fun observeUserData(): Flow<User?> {
        return callbackFlow {
            val currentUserUid = firebaseAuth.currentUser?.uid
            val docRef = currentUserUid?.let { firebaseFirestore.collection("users").document(it) }
            val subscription = docRef?.addSnapshotListener { value, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (value != null) {
                    val user = value.toObject(User::class.java)
                    trySend(user)
                }
            }
            awaitClose {
                subscription?.remove()
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun sendFriendshipRequest(
        senderId: String,
        receiverId: String,
        senderName: String
    ) {
        val db = FirebaseFirestore.getInstance()
        val friendshipDocRef = db.collection("friendships")

        val query1 = friendshipDocRef
            .whereEqualTo("senderId", senderId)
            .whereEqualTo("receiverId", receiverId)

        val query2 = friendshipDocRef
            .whereEqualTo("senderId", receiverId)
            .whereEqualTo("receiverId", senderId)

        try {
            val result1 = query1.get().await()
            val result2 = query2.get().await()

            if (result1.isEmpty && result2.isEmpty) {
                val requestData = hashMapOf(
                    "senderId" to senderId,
                    "receiverId" to receiverId,
                    "status" to "pending",
                    "senderName" to senderName
                )
                friendshipDocRef.add(requestData).await()
            }
        } catch (e: Exception) {
            Log.e("TAG", "Failed sending request: ${e.message}")
        }
    }

    override suspend fun acceptFriendshipRequest(friendshipId: String) {
        withContext(Dispatchers.IO) {
            val myUserId = firebaseAuth.currentUser?.uid
                ?: throw Exception("User is not authenticated.")

            val friendshipRef = firebaseFirestore.collection("friendships").document(friendshipId)

            val friendship = friendshipRef.get().await().toObject(FriendshipRequest::class.java)

            if (friendship == null || friendship.receiverId != myUserId) {
                throw Exception("This friendship request is not for you.")
            }

            friendshipRef.update("status", "accepted").await()
        }
    }

    override suspend fun declineFriendshipRequest(friendshipId: String) {
        withContext(Dispatchers.IO) {
            val friendshipRef = firebaseFirestore.collection("friendships").document(friendshipId)
            try {
                friendshipRef.delete().await()
            } catch (e: Exception) {
                throw Exception("Failed to decline friendship request: ${e.message}")
            }
        }
    }

    override fun observeFriendsList(userId: String): Flow<List<Friend>> {
        return callbackFlow {
            val docRef =
                firebaseFirestore.collection("users").document(userId).collection("friends")

            val subscription = docRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val friends = snapshot?.documents?.mapNotNull { it.toObject(Friend::class.java) }
                    ?: emptyList()
                trySend(friends)
            }
            awaitClose { subscription.remove() }
        }.flowOn(Dispatchers.IO)
    }

    override fun observeFriendshipRequestStatus(
        myUserId: String,
        otherUserId: String
    ): Flow<FriendshipRequest?> {
        val myUserIsSenderFlow: Flow<FriendshipRequest?> = callbackFlow {
            val query = firebaseFirestore.collection("friendships")
                .whereEqualTo("senderId", myUserId)
                .whereEqualTo("receiverId", otherUserId)

            val listener = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val friendship =
                    snapshot?.documents?.firstOrNull()?.toObject(FriendshipRequest::class.java)
                        ?.copy(id = snapshot.documents.first().id)
                trySend(friendship)
            }

            awaitClose { listener.remove() }
        }

        val myUserIsReceiverFlow: Flow<FriendshipRequest?> = callbackFlow {
            val query = firebaseFirestore.collection("friendships")
                .whereEqualTo("senderId", otherUserId)
                .whereEqualTo("receiverId", myUserId)

            val listener = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val friendship =
                    snapshot?.documents?.firstOrNull()?.toObject(FriendshipRequest::class.java)
                        ?.copy(id = snapshot.documents.first().id)
                trySend(friendship)
            }

            awaitClose { listener.remove() }
        }

        return combine(myUserIsSenderFlow, myUserIsReceiverFlow) { friendship1, friendship2 ->
            friendship1 ?: friendship2
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun createChat(userUid: String, friendUid: String): String {
        val functions = Firebase.functions
        val data = hashMapOf(
            "friendUid" to friendUid
        )
        val result = functions
            .getHttpsCallable("createChat")
            .call(data)
            .await()
        val responseData = result.data as Map<String, Any>

        return responseData["chatId"] as String
    }

    override suspend fun sendMessage(chatId: String, userUid: String, messageText: String) {
        val functions = Firebase.functions

        val data = hashMapOf(
            "chatId" to chatId,
            "messageText" to messageText
        )

        functions
            .getHttpsCallable("sendMessage")
            .call(data)
            .await()
    }

    override fun observeChatMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val messagesRef =
            firebaseFirestore.collection("chats").document(chatId).collection("messages")

        val subscription = messagesRef
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    if (exception.code == FirebaseFirestoreException.Code.NOT_FOUND) {
                        trySend(emptyList()).isSuccess
                    } else {
                        cancel(message = "Error getting messages", cause = exception)
                    }
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val messages = snapshot.documents.map { doc ->
                        doc.toObject(Message::class.java)!!
                    }
                    trySend(messages).isSuccess
                } else {
                    trySend(emptyList()).isSuccess
                }
            }

        awaitClose { subscription.remove() }
    }.flowOn(Dispatchers.IO)

    override suspend fun updateUserProfile(updatedUser: User): Flow<Unit> = flow {
        val userId = updatedUser.uid ?: throw Exception("User not authenticated")
        val userDocRef = firebaseFirestore.collection("users").document(userId)
        try {
            val updates = mutableMapOf<String, Any?>()
            updates["name"] = updatedUser.name
            updates["description"] = updatedUser.description
            updates["imageUri"] = updatedUser.imageUri

            userDocRef.update(updates).await()
            emit(Unit)
        } catch (e: Exception) {
            throw Exception("Failed to update user profile: ${e.message}")
        }

    }.flowOn(Dispatchers.IO)

    override suspend fun checkChatExists(myUserId: String, friendUid: String): Boolean {
        val data = hashMapOf(
            "myUid" to myUserId,
            "friendUid" to friendUid
        )
        val result = Firebase.functions
            .getHttpsCallable("checkChatExists")
            .call(data)
            .await()

        val responseData = result.data as Map<String, Any>
        return responseData["exists"] as Boolean
    }

    override fun observeFriendInvitationsCount(userId: String): Flow<Long> = callbackFlow {
        val docRef = firebaseFirestore.collection("users").document(userId)
        val subscription = docRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                close(exception)
                return@addSnapshotListener
            }
            val count = snapshot?.getLong("unreadFriendInvitations") ?: 0L
            trySend(count).isSuccess
        }
        awaitClose { subscription.remove() }
    }.flowOn(Dispatchers.IO)

    override fun observeGameInvitationsCount(userId: String): Flow<Long> = callbackFlow {
        val docRef = firebaseFirestore.collection("users").document(userId)
        val subscription = docRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                close(exception)
                return@addSnapshotListener
            }
            val count = snapshot?.getLong("unreadGameInvitations") ?: 0L
            trySend(count).isSuccess
        }
        awaitClose { subscription.remove() }
    }.flowOn(Dispatchers.IO)

    override fun observeFriendshipRequests(userId: String): Flow<List<FriendshipRequest>> {
        return callbackFlow {
            val docRef = firebaseFirestore.collection("friendships")
                .whereEqualTo("receiverId", userId)
                .whereEqualTo("status", "pending")

            val subscription = docRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val requests = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(FriendshipRequest::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                val senderIds = requests.mapNotNull { it.senderId }.toSet()

                if (senderIds.isEmpty()) {
                    trySend(emptyList()).isSuccess
                    return@addSnapshotListener
                }

                firebaseFirestore.collection("users")
                    .whereIn("uid", senderIds.toList())
                    .get()
                    .addOnSuccessListener { sendersSnapshot ->
                        val sendersMap = sendersSnapshot.documents.associateBy({ it.id },
                            { it.toObject(User::class.java) })

                        val requestsWithSenderData = requests.map { request ->
                            val sender = sendersMap[request.senderId]
                            request.copy(
                                senderName = sender?.name,
                                senderImageUri = sender?.imageUri
                            )
                        }
                        trySend(requestsWithSenderData).isSuccess
                    }
                    .addOnFailureListener { e ->
                        close(e)
                    }
            }

            awaitClose { subscription.remove() }
        }.flowOn(Dispatchers.IO)
    }

    override fun observeGameRequest(userId: String): Flow<List<GameRequest>> = callbackFlow {
        val requestsRef = firebaseFirestore.collection("gameInvitations")
            .whereEqualTo("receiverId", userId)
            .whereEqualTo("status", "pending")

        val subscription = requestsRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val gameRequests = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(GameRequest::class.java)
                }
                trySend(gameRequests)
            } else {
                trySend(emptyList())
            }
        }

        awaitClose { subscription.remove() }
    }

    override suspend fun decrementFriendInvitationCount(userId: String) {
        withContext(Dispatchers.IO) {
            val userRef = firebaseFirestore.collection("users").document(userId)
            userRef.update("unreadFriendInvitations", FieldValue.increment(-1))
        }
    }

    override suspend fun decrementGameInvitationCount(userId: String) {
        withContext(Dispatchers.IO) {
            val userRef = firebaseFirestore.collection("users").document(userId)
            userRef.update("unreadGameInvitations", FieldValue.increment(-1))
        }
    }
}