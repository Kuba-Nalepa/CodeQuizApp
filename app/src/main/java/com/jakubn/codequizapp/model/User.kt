package com.jakubn.codequizapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val uid: String? = null,
    val name: String? = null,
    val email: String? = null,
    val imageUri: String? = null,
    val gamesPlayed: Int = 0,
    val wins: Int = 0,
    val score: Int = 0,
    val winRatio: Float = 0f,
    val description: String? = null,
    val fcmToken: String? = null
): Parcelable
