package com.jakubn.codequizapp.domain.repositoryImpl

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.jakubn.codequizapp.domain.model.User
import com.jakubn.codequizapp.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
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
                email = email,
                score = 0,
                imageUrl = null
            )

            firebaseFirestore.collection("users").document(firebaseUser.uid).set(user).await()
            emit(user)
        }.flowOn(Dispatchers.IO) // Errors will be thrown naturally within the flow
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
        }
    }

    override fun signOutUser(): Boolean {
        firebaseAuth.signOut()
        return true

    }
}