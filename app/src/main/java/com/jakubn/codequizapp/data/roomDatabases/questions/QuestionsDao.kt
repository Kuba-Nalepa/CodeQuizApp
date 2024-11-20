package com.jakubn.codequizapp.data.roomDatabases.questions

import androidx.room.Dao
import androidx.room.Upsert

@Dao
interface QuestionsDao {
    @Upsert
    suspend fun insertQuestion(question: Question)

}