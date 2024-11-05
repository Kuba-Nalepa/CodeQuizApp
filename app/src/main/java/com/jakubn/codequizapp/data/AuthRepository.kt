package com.jakubn.codequizapp.data

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser: FirebaseUser?

    suspend fun login(email: String, psw: String): Resource<FirebaseUser>
    suspend fun signUp(name: String, email: String, psw: String): Resource<FirebaseUser>
    fun logOut()
}