package com.jakubn.codequizapp.domain.repositoryImpl


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.jakubn.codequizapp.domain.model.Game
import com.jakubn.codequizapp.domain.model.Lobby
import com.jakubn.codequizapp.domain.model.Question
import com.jakubn.codequizapp.domain.model.User
import com.jakubn.codequizapp.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepositoryImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : GameRepository {

    override suspend fun createGame(
        questionCategory: String,
        questionQuantity: Int,
        questionDuration: Int
    ): Flow<String> {
        return flow {
            // setting lobby founder
            val userList = firebaseFirestore.collection("users").get().await()
            val founder = userList.toObjects(User::class.java).find {
                it.uid == firebaseAuth.currentUser?.uid
            }

            // getting questions from server
            val questionList = firebaseDatabase.getReference("questions").get().await().children.mapNotNull { snapshot ->
                snapshot.getValue(Question::class.java)
            }
            val filteredQuestions = questionList.filter {
                questionCategory == it.category
            }.shuffled().take(questionQuantity)

            // creating game
            val lobby = Lobby(founder, null)

            val gameId = firebaseDatabase.reference.child("games").push().key
                ?: throw Exception("Failed creating key")

            val game = Game(gameId, filteredQuestions, lobby, questionDuration)

            firebaseDatabase.reference.child("games").child(gameId).setValue(game)

            emit(gameId)
        }
    }

    override suspend fun getLobbyData(gameId: String): Flow<Lobby> {
        return flow {
            val gameReference = firebaseDatabase.getReference("games").child(gameId)
            val lobby = gameReference.child("lobby").get().await().getValue(Lobby::class.java)
                ?: throw Exception("Failed fetching lobby data")

            emit(lobby)
        }
    }
}