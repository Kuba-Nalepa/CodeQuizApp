package com.jakubn.codequizapp.room.answer

import androidx.room.Dao
import androidx.room.Upsert

@Dao
interface AnswersDao {

    @Upsert
    suspend fun insertAnswer(correctAnswer: Answer)
}