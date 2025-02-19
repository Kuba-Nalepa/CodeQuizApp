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
    ): Flow<String> = flow {
        val questionList = firebaseDatabase.getReference("questions").get()
            .await().children.mapNotNull { snapshot ->
                snapshot.getValue(Question::class.java)
            }
        val filteredQuestions = questionList.filter {
            questionCategory == it.category
        }.shuffled().take(questionQuantity)

        val gameId = firebaseDatabase.reference.child("games").push().key
            ?: throw Exception("Failed creating key")

        val game = Game(
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

    override suspend fun manageGameState(gameId: String, state: Boolean) {
        firebaseDatabase.getReference("games").child(gameId).updateChildren(
            hashMapOf("gameInProgress" to state) as Map<String, Any>
        ).await()
    }

    override suspend fun listenGameDataChanges(gameId: String): Flow<Game> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val game = snapshot.getValue(Game::class.java)
                if (game != null) {
                    trySend(game)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(Exception(error.message))
            }
        }

        val gameRef = firebaseDatabase.getReference("games").child(gameId)
        gameRef.addValueEventListener(listener)

        awaitClose {
            gameRef.removeEventListener(listener)
        }
    }

    override suspend fun getGameData(gameId: String): Flow<Game> = flow {
        val gameSnapshot = firebaseDatabase.getReference("games").child(gameId).get().await()
        val game = gameSnapshot.getValue(Game::class.java)
        if (gameSnapshot != null) {
            if (game != null) {
                emit(game)
            }
        }
    }

    override suspend fun addMemberToLobby(gameId: String, user: User) {
        firebaseDatabase.reference.child("games").child(gameId).child("lobby").child("member")
            .setValue(user).await()
    }

    override suspend fun removeMemberFromLobby(gameId: String) {
        firebaseDatabase.reference.child("games").child(gameId).child("lobby").child("member")
            .removeValue().await()
    }

    override suspend fun deleteLobby(gameId: String) {
        firebaseDatabase.reference.child("games").child(gameId).removeValue().await()
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

        readinessKey?.let { key ->
            firebaseDatabase.reference.child("games")
                .child(gameId)
                .child("lobby")
                .updateChildren(hashMapOf(key to userReadinessStatus) as Map<String, Any>).await()
        }
    }

    override suspend fun getGamesList(): Flow<List<Game>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val games = snapshot.children.mapNotNull {
                    it.getValue(Game::class.java)
                }
                trySend(games)
            }

            override fun onCancelled(error: DatabaseError) {
                close(Exception(error.message))
            }
        }

        val reference = firebaseDatabase.getReference("games")
        reference.addValueEventListener(listener)

        awaitClose {
            reference.removeEventListener(listener)
        }
    }

    override suspend fun saveUserGameStats(
        gameId: String,
        lobby: Lobby,
        user: User,
        answersList: List<Int>,
        correctAnswersQuantity: Int,
        points: Int
    ) {
        val userPointsKey = when (user.uid) {
            lobby.founder?.uid -> "founderPoints"
            lobby.member?.uid -> "memberPoints"
            else -> null
        }

        userPointsKey?.let { key ->
            firebaseDatabase.reference.child("games")
                .child(gameId)
                .child("lobby")
                .updateChildren(hashMapOf(key to points) as Map<String, Any>).await()
        }

        val userCorrectAnswersNumberKey = when (user.uid) {
            lobby.founder?.uid -> "founderCorrectAnswersQuantity"
            lobby.member?.uid -> "memberCorrectAnswersQuantity"
            else -> null
        }

        userCorrectAnswersNumberKey?.let { key ->
            firebaseDatabase.reference.child("games")
                .child(gameId)
                .child("lobby")
                .updateChildren(hashMapOf(key to correctAnswersQuantity) as Map<String, Any>).await()
        }

        val userAnswersListKey = when (user.uid) {
            lobby.founder?.uid -> "founderAnswersList"
            lobby.member?.uid -> "memberAnswersList"
            else -> null
        }

        userAnswersListKey?.let { key ->
            firebaseDatabase.reference.child("games")
                .child(gameId)
                .child("lobby")
                .updateChildren(hashMapOf(key to answersList) as Map<String, Any>).await()
        }
    }

    override suspend fun setUserFinishedGame(
        gameId: String,
        lobby: Lobby,
        user: User,
        hasFinished: Boolean
    ) {
        val userFinishedKey = when (user.uid) {
            lobby.founder?.uid -> "hasFounderFinishedGame"
            lobby.member?.uid -> "hasMemberFinishedGame"
            else -> null
        }

        userFinishedKey?.let { key ->
            firebaseDatabase.reference.child("games")
                .child(gameId)
                .child("lobby")
                .updateChildren(hashMapOf(key to hasFinished) as Map<String, Any>).await()
        }
    }

    override suspend fun setUserLeftGame(
        game: Game,
        user: User,
        hasLeft: Boolean
    ) {
        val userLeftGameKey = when (user.uid) {
            game.lobby?.founder?.uid -> "hasFounderLeftGame"
            game.lobby?.member?.uid -> "hasMemberLeftGame"
            else -> null
        }

        userLeftGameKey?.let { key ->
            firebaseDatabase.reference.child("games")
                .child(game.gameId)
                .child("lobby")
                .updateChildren(hashMapOf(key to hasLeft) as Map<String, Any>).await()
        }
    }

    override suspend fun archiveGame(game: Game) {
        firebaseDatabase.reference.child("archivedGames").child(game.gameId).setValue(game).await()
    }

    override suspend fun deleteGame(game: Game) {
        firebaseDatabase.reference.child("games").child(game.gameId).removeValue().await()
    }
}