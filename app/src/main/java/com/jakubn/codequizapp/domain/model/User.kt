package com.jakubn.codequizapp.domain.model

data class User(val uid: String, val name: String,val email: String, val imageUrl: String?, val score: Int)
