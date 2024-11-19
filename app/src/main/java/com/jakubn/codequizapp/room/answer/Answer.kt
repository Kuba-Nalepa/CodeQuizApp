package com.jakubn.codequizapp.room.answer

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "answers")
data class Answer(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val questionId: Int,
    val correctA: String,
    val correctB: String,
    val correctC: String,
    val correctD: String,
)
