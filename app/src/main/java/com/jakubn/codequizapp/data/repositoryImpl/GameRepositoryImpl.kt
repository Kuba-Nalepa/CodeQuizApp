package com.jakubn.codequizapp.data.repositoryImpl


import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jakubn.codequizapp.domain.model.Game
import com.jakubn.codequizapp.domain.model.Lobby
import com.jakubn.codequizapp.domain.model.Question
import com.jakubn.codequizapp.domain.model.User
import com.jakubn.codequizapp.data.repository.GameRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepositoryImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) : GameRepository {

    override suspend fun createGame(
        questionCategory: String,
        questionQuantity: Int,
        questionDuration: Int,
        founder: User
    ): Flow<String> {
        return flow {
            // getting questions from server
            val questionList = firebaseDatabase.getReference("questions").get()
                .await().children.mapNotNull { snapshot ->
                    snapshot.getValue(Question::class.java)
                }
            val filteredQuestions = questionList.filter {
                questionCategory == it.category
            }.shuffled().take(questionQuantity)

            val gameId = firebaseDatabase.reference.child("games").push().key
                ?: throw Exception("Failed creating key")

            val game = Game(gameId, questionCategory, filteredQuestions, Lobby(founder) ,questionDuration)

            firebaseDatabase.reference.child("games").child(gameId).setValue(game)

            emit(gameId)
        }
    }

    override suspend fun getLobbyData(gameId: String): Flow<Lobby?> {
        return callbackFlow {

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lobby = snapshot.getValue(Lobby::class.java)

                    trySend(lobby)
                }

                override fun onCancelled(error: DatabaseError) {
                    throw Exception(error.message)                }
            }

            val lobbyRef = firebaseDatabase.getReference("games").child(gameId).child("lobby")

            val lobbyDataFirst = lobbyRef.get().await().getValue(Lobby::class.java)
                ?: throw Exception("Failed fetching lobby data")
            trySend(lobbyDataFirst)

            lobbyRef.addValueEventListener(listener)
            awaitClose {
                lobbyRef.removeEventListener(listener)
            }
        }
    }

    override suspend fun addMemberToLobby(gameId: String, user: User) {
            firebaseDatabase.reference.child("games").child(gameId).child("lobby").child("member").setValue(user).await()

        }

    override suspend fun removeMemberFromLobby(gameId: String) {
        firebaseDatabase.reference.child("games").child(gameId).child("lobby").child("member").removeValue()

    }

    override suspend fun deleteLobby(gameId: String) {
        firebaseDatabase.reference.child("games").child(gameId).removeValue()
    }

    override suspend fun getGamesList(): Flow<List<Game>> {
        return callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val games = snapshot.children.mapNotNull {
                        it.getValue(Game::class.java) ?: throw Exception("Failed fetching games")
                    }
                    trySend(games)
                }

                override fun onCancelled(error: DatabaseError) {
                    throw Exception(error.message)
                }

            }
            val reference = firebaseDatabase.getReference("games")

            reference.addValueEventListener(listener)
            awaitClose {
                reference.removeEventListener(listener)
            }
        }
    }
}