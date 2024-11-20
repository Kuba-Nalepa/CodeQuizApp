package com.jakubn.codequizapp.data.roomDatabases.answers

import androidx.room.Dao
import androidx.room.Upsert

@Dao
interface AnswersDao {

    @Upsert
    suspend fun insertAnswer(correctAnswer: Answer)
}