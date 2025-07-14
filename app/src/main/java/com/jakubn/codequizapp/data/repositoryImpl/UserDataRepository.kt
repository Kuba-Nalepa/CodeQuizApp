package com.jakubn.codequizapp.data.repositoryImpl

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.jakubn.codequizapp.model.User
import com.jakubn.codequizapp.data.repository.UserDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

    override suspend fun getUsers(): Flow<List<User>> = callbackFlow{
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
}