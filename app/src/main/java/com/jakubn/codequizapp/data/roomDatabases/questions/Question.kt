package com.jakubn.codequizapp.data.roomDatabases.questions

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class Question(
    @PrimaryKey(autoGenerate = false)
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