package com.jakubn.codequizapp.room.question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class QuestionsViewModel(
    private val dao: QuestionsDao
): ViewModel() {

    fun addData(questions: List<Question>) = viewModelScope.launch {
            questions.forEach { question ->
                dao.insertQuestion(question)
            }
    }

}