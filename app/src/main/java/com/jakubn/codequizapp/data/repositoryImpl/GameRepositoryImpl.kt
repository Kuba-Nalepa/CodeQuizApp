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
            val questionList = firebaseDatabase.getReference("questions").get()
                .await().children.mapNotNull { snapshot ->
                    snapshot.getValue(Question::class.java)
                }
            val filteredQuestions = questionList.filter {
                questionCategory == it.category
            }.shuffled().take(questionQuantity)

            val gameId = firebaseDatabase.reference.child("games").push().key
                ?: throw Exception("Failed creating key")

            val game =
                Game(
                    gameId,
                    false,
                    questionCategory,
                    filteredQuestions,
                    Lobby(founder),
                    questionDuration
                )

            firebaseDatabase.reference.child("games").child(gameId).setValue(game)

            emit(gameId)
        }
    }

    override suspend fun startGame(gameId: String) {
        firebaseDatabase.getReference("games").child(gameId).updateChildren(
            hashMapOf<String, Any>("isGameStarted" to true))
    }

    override suspend fun getGameData(gameId: String): Flow<Game?> {
        return callbackFlow {

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val game = snapshot.getValue(Game::class.java)

                    trySend(game)
                }

                override fun onCancelled(error: DatabaseError) {
                    throw Exception(error.message)
                }
            }

            val gameRef = firebaseDatabase.getReference("games").child(gameId)

            val gameData = gameRef.get().await().getValue(Game::class.java)
                ?: throw Exception("Failed fetching game data")
            trySend(gameData)

            gameRef.addValueEventListener(listener)
            awaitClose {
                gameRef.removeEventListener(listener)
            }
        }
    }

    override suspend fun addMemberToLobby(gameId: String, user: User) {
        firebaseDatabase.reference.child("games").child(gameId).child("lobby").child("member")
            .setValue(user).await()

    }

    override suspend fun removeMemberFromLobby(gameId: String) {
        firebaseDatabase.reference.child("games").child(gameId).child("lobby").child("member")
            .removeValue()

    }

    override suspend fun deleteLobby(gameId: String) {
        firebaseDatabase.reference.child("games").child(gameId).removeValue()
    }

    override suspend fun changeUserReadinessStatus(
        gameId: String,
        lobby: Lobby,
        user: User,
        userReadinessStatus: Boolean
    ) {

        val readinessKey = when (user.uid) {
            lobby.founder?.uid -> "isFounderReady"
            lobby.member?.uid -> "isMemberReady"
            else -> null
        }

        readinessKey?.let {
            firebaseDatabase.reference.child("games")
                .child(gameId)
                .child("lobby")
                .updateChildren(hashMapOf<String, Any>(it to userReadinessStatus))
        }
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

    override suspend fun saveUserGamePoints(gameId: String, lobby: Lobby, user: User, points: Int) {

        val userPointsKey = when (user.uid) {
            lobby.founder?.uid -> "founderPoints"
            lobby.member?.uid -> "memberPoints"
            else -> null
        }

        userPointsKey?.let {
            firebaseDatabase.reference.child("games")
                .child(gameId)
                .child("lobby")
                .updateChildren(hashMapOf<String, Any>(it to points)).await()
        }
    }

    override suspend fun setUserFinishedGame(gameId: String, lobby: Lobby, user: User, hasFinished: Boolean) {

        val userFinishedKey = when (user.uid) {
            lobby.founder?.uid -> "hasFounderFinishedGame"
            lobby.member?.uid -> "hasMemberFinishedGame"
            else -> null
        }

        userFinishedKey?.let {
            firebaseDatabase.reference.child("games")
                .child(gameId)
                .child("lobby")
                .updateChildren(hashMapOf<String, Any>(it to hasFinished)).await()
        }
    }
}