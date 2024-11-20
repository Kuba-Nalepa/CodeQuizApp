package com.jakubn.codequizapp.data.roomDatabases.answers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AnswersViewModel(
    private val dao: AnswersDao
): ViewModel() {

    fun addData(questions: List<Answer>) = viewModelScope.launch {
        questions.forEach { answer ->
            dao.insertAnswer(answer)
        }
    }
}