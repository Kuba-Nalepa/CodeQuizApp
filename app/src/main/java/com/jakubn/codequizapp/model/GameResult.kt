package com.jakubn.codequizapp.model

sealed class GameResult {
    abstract val correctAnswers: Int
    abstract val totalQuestions: Int

    data class Win(
        val winner: User,
        val loser: User,
        val winnerPoints: Int,
        val loserPoints: Int,
        override val correctAnswers: Int,
        override val totalQuestions: Int
    ) : GameResult()

    data class Lose(
        val winner: User,
        val loser: User,
        val winnerPoints: Int,
        val loserPoints: Int,
        override val correctAnswers: Int,
        override val totalQuestions: Int
    ) : GameResult()

    data class Tie(
        val firstUser: User,
        val secondUser: User,
        val firstUserPoints: Int,
        val secondUserPoints: Int,
        override val correctAnswers: Int,
        override val totalQuestions: Int
    ) : GameResult()
}