package com.jakubn.codequizapp.room.question

import androidx.room.Dao
import androidx.room.Upsert

@Dao
interface QuestionsDao {
    @Upsert
    suspend fun insertQuestion(question: Question)

}