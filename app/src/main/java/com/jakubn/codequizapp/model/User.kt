package com.jakubn.codequizapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class User(
    val uid: String? = null,
    val name: String? = null,
    val email: String? = null,
    var friends: @RawValue List<Friend>? = null,
    val imageUri: String? = null,
    val gamesPlayed: Int = 0,
    val wins: Int = 0,
    val score: Int = 0,
    val winRatio: Float = 0f,
    val description: String? = null
): Parcelable
