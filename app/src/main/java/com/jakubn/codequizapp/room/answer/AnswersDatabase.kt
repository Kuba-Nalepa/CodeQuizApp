package com.jakubn.codequizapp.room.answer

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Answer::class], version = 1, exportSchema = false)
abstract class AnswersDatabase: RoomDatabase() {
    abstract val dao: AnswersDao
}