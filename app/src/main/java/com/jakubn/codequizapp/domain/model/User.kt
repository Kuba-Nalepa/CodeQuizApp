package com.jakubn.codequizapp.domain.model

data class User(
    val uid: String? = null,
    val name: String? = null,
    val email: String? = null,
    var friends: List<Friend>? = null,
    val imageUri: String? = null,
    val gamesPlayed: Int = 0,
    val wins: Int = 0,
    val score: Int = 0,
    val winRatio: Int = 0
)
