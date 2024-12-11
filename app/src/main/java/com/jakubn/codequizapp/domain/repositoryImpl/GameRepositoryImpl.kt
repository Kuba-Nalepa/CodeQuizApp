package com.jakubn.codequizapp.domain.repositoryImpl


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.jakubn.codequizapp.domain.model.Game
import com.jakubn.codequizapp.domain.model.Lobby
import com.jakubn.codequizapp.domain.model.Question
import com.jakubn.codequizapp.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepositoryImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseAuth: FirebaseAuth
) : GameRepository {

    override suspend fun createGame(
        questionCategory: String,
        questionQuantity: Int,
        questionDuration: Int
    ): Flow<Boolean> {
        return flow {
            val currentUser = firebaseAuth.currentUser?.displayName

            // getting questions from server
            val reference = firebaseDatabase.getReference("questions")
            val dataSnapshot = reference.get().await()
            val questionList = dataSnapshot.children.mapNotNull { snapshot ->
                snapshot.getValue(Question::class.java)
            }
            val filteredQuestions = questionList.filter {
                questionCategory == it.category
            }.shuffled().take(questionQuantity)

            // creating game
            val lobby = Lobby(currentUser, "")
            val game = Game(filteredQuestions, lobby, questionDuration)
            val key = firebaseDatabase.reference.child("games").push().key
                ?: throw Exception("Failed creating key")

            val result = firebaseDatabase.reference.child("games").child(key).setValue(game).isSuccessful


            emit(result)
        }
    }
}