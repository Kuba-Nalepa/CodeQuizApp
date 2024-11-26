package com.jakubn.codequizapp.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class Question (
    val id: Long,
    val question: String,
    val description: String = "",
    val answers: Answers,

    @SerialName("multiple_correct_answers")
    val multipleCorrectAnswers: String,

    @SerialName("correct_answers")
    val correctAnswers: CorrectAnswers,

    @SerialName("correct_answer")
    val correctAnswer: JsonElement? = null,

    val explanation: String = "",
    val tip: JsonElement? = null,
    val tags: List<Tag>,
    val category: String,
    val difficulty: String
)

@Serializable
data class Answers (
    @SerialName("answer_a")
    val answerA: String? = null,

    @SerialName("answer_b")
    val answerB: String? = null,

    @SerialName("answer_c")
    val answerC: String? = null,

    @SerialName("answer_d")
    val answerD: String? = null,

    @SerialName("answer_e")
    val answerE: JsonElement? = null,

    @SerialName("answer_f")
    val answerF: JsonElement? = null
)

@Serializable
data class CorrectAnswers (
    @SerialName("answer_a_correct")
    val answerACorrect: String? = null,

    @SerialName("answer_b_correct")
    val answerBCorrect: String? = null,

    @SerialName("answer_c_correct")
    val answerCCorrect: String? = null,

    @SerialName("answer_d_correct")
    val answerDCorrect: String? = null,

    @SerialName("answer_e_correct")
    val answerECorrect: String? = null,

    @SerialName("answer_f_correct")
    val answerFCorrect: String? = null
)

@Serializable
data class Tag (
    val name: String? = null
)
