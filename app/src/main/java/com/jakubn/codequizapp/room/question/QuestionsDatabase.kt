package com.jakubn.codequizapp.room.question

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Question::class], version = 1, exportSchema = false)
abstract class QuestionsDatabase: RoomDatabase() {
    abstract val dao: QuestionsDao
}