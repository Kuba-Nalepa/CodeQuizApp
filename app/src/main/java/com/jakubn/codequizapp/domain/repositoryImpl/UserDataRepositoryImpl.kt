package com.jakubn.codequizapp.domain.repositoryImpl

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jakubn.codequizapp.domain.model.User
import com.jakubn.codequizapp.domain.repository.UserDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : UserDataRepository {

    override suspend fun getUserData(): Flow<User> {
        return flow {
            val currentUserUid = firebaseAuth.currentUser?.uid ?: throw Exception("User uid does not exist")
            val user = firebaseFirestore.collection("users").document(currentUserUid).get().await().toObject(
                User::class.java
            ) ?: throw Exception("Data fetching failed")

            emit(user)
        }.flowOn(Dispatchers.IO)
    }

}