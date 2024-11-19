package com.jakubn.codequizapp.data.classes


data class Question (
    val id: Int,
    val questionTitle: String,
    val answerA: String,
    val answerB: String,
    val answerC: String,
    val answerD: String,
    val multipleCorrectAnswers: String,
    val category: String,
    val difficulty: String,
    val played: Boolean = false
)