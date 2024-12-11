package com.jakubn.codequizapp.domain.model

import com.google.firebase.database.PropertyName

data class Question(
    var id: Long? = null,
    var question: String? = null,
    var description: String = "",
    var answers: Answers? = null,

    @set:PropertyName("multiple_correct_answers")
    @get:PropertyName("multiple_correct_answers")
    var multipleCorrectAnswers: String? = null,

    @set:PropertyName("correct_answers") 
    @get:PropertyName("correct_answers")
    var correctAnswers: CorrectAnswers? = null,

    @set:PropertyName("correct_answer")
    @get:PropertyName("correct_answer")
    var correctAnswer: String? = null, 

    var explanation: String = "",
    var tip: String? = null,
    var tags: List<Tag>? = null,
    var category: String? = null,
    var difficulty: String? = null
)


data class Answers(
    @set:PropertyName("answer_a")
    @get:PropertyName("answer_a") 
    var answerA: String? = null,

    @set:PropertyName("answer_b") 
    @get:PropertyName("answer_b") 
    var answerB: String? = null,

    @set:PropertyName("answer_c") 
    @get:PropertyName("answer_c") 
    var answerC: String? = null,

    @set:PropertyName("answer_d") 
    @get:PropertyName("answer_d") 
    var answerD: String? = null,

    @set:PropertyName("answer_e") 
    @get:PropertyName("answer_e") 
    var answerE: String? = null,        

    @set:PropertyName("answer_f") 
    @get:PropertyName("answer_f") 
    var answerF: String? = null         
)


data class CorrectAnswers(
    @set:PropertyName("answer_a_correct") 
    @get:PropertyName("answer_a_correct") 
    var answerACorrect: String? = null,

    @set:PropertyName("answer_b_correct") 
    @get:PropertyName("answer_b_correct") 
    var answerBCorrect: String? = null,

    @set:PropertyName("answer_c_correct") 
    @get:PropertyName("answer_c_correct") 
    var answerCCorrect: String? = null,

    @set:PropertyName("answer_d_correct") 
    @get:PropertyName("answer_d_correct") 
    var answerDCorrect: String? = null,

    @set:PropertyName("answer_e_correct") 
    @get:PropertyName("answer_e_correct") 
    var answerECorrect: String? = null,

    @set:PropertyName("answer_f_correct") 
    @get:PropertyName("answer_f_correct") 
    var answerFCorrect: String? = null
)


data class Tag(
    var name: String? = null
)

data class Game(
    var questions: List<Question>? = null,
    var lobby: Lobby? = null,
    var questionDuration: Int? = null
)

data class Lobby(
    var founder: String? = null,
    var member: String? = null
)