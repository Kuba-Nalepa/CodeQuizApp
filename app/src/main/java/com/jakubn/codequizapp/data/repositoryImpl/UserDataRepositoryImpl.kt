package com.jakubn.codequizapp.data.repositoryImpl

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.jakubn.codequizapp.domain.model.User
import com.jakubn.codequizapp.data.repository.UserDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
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

    override suspend fun updateUserData(user: User, score: Int, hasCurrentUserWon: Boolean) {
        withContext(Dispatchers.IO) {

            val userId = user.uid ?: throw Exception("User not authenticated")
            val userDocRef = firebaseFirestore.collection("users").document(userId)

            try {
                val document = userDocRef.get().await()
                if (!document.exists()) throw Exception("User document not found")

                val currentWins = document.getLong("wins") ?: 0L
                val currentGamesPlayed = document.getLong("gamesPlayed") ?: 0L

                val winsIncrement = if (hasCurrentUserWon) 1L else 0L
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
}