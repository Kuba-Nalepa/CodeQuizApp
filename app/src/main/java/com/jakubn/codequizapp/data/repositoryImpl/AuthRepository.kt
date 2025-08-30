package com.jakubn.codequizapp.data.repositoryImpl

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.jakubn.codequizapp.model.User
import com.jakubn.codequizapp.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseMessaging: FirebaseMessaging
) : AuthRepository {

    override suspend fun signUpUser(
        name: String,
        email: String,
        password: String
    ): Flow<User> {
        return flow {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("User creation failed")

            val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(name).build()
            firebaseUser.updateProfile(profileUpdates).await()

            val user = User(
                uid = firebaseUser.uid,
                name = name,
                email = email
            )

            firebaseFirestore.collection("users").document(firebaseUser.uid).set(user).await()
            emit(user)
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun signInUser(
        email: String,
        password: String
    ): Flow<User> {
        return flow {

            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("User sign-in failed")

            val documentSnapshot =
                firebaseFirestore.collection("users").document(firebaseUser.uid).get()
                    .await()
            val user = documentSnapshot.toObject(User::class.java)
                ?: throw Exception("User data not found")

            emit(user)
        }.flowOn(Dispatchers.IO)
    }

    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override fun signOutUser() {
        firebaseAuth.signOut()
    }

    override suspend fun saveFCMTokenToFirestore(userId: String) {
        try {
            val token = firebaseMessaging.token.await()
            firebaseFirestore.collection("users").document(userId).update("fcmToken", token).await()
        } catch (e: Exception) {
            // Obsługa błędu TODO()
        }
    }
}