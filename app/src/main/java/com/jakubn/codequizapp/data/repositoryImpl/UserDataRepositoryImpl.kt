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

    override suspend fun updateUserData(score: Int, hasUserWon: Boolean) {
        withContext(Dispatchers.IO) {
            val currentUser = firebaseAuth.currentUser
            currentUser?.uid?.let { userId ->
                if (hasUserWon) {
                    val userDocRef = firebaseFirestore.collection("users").document(userId)

                    val documentSnapshot = userDocRef.get().await()
                    val currentWins = documentSnapshot.getLong("wins") ?: 0
                    val currentGamesPlayed = documentSnapshot.getLong("gamesPlayed") ?: 0

                    val updatedWins = currentWins + 1
                    val updatedGamesPlayed = currentGamesPlayed + 1
                    val winRatio = if (updatedGamesPlayed > 0) {
                        updatedWins.toDouble() / updatedGamesPlayed.toDouble()
                    } else {
                        0.0
                    }

                    userDocRef.update(
                        "score", FieldValue.increment(score.toDouble()),
                        "gamesPlayed", FieldValue.increment(1),
                        "wins", FieldValue.increment(1),
                        "winRatio", winRatio
                    ).await()
                }
            } ?: throw Exception("Failed updating users data")
        }
    }

}