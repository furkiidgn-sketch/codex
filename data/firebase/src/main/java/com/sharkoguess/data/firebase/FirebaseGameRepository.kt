package com.sharkoguess.data.firebase

import kotlin.collections.buildMap

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.sharkoguess.domain.model.GameConfig
import com.sharkoguess.domain.model.Question
import com.sharkoguess.domain.repository.GameMatchState
import com.sharkoguess.domain.repository.GameRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseGameRepository @Inject constructor(
    private val auth: FirebaseAuth,
override suspend fun findOrCreateMatch(config: GameConfig): String {
        val callable = functions.getHttpsCallable("findOrCreateMatch")
        val result = callable.call(config.toMap()).await()
        val data = result.data as Map<*, *>
        return data["matchId"] as String
    }

    private val firestore: FirebaseFirestore,
    private val functions: FirebaseFunctions
) : GameRepository {

    override suspend fun generateQuestions(config: GameConfig): List<Question> {
        val callable = functions.getHttpsCallable("generateQuestions")
        val result = callable.call(config.toMap()).await()
        val data = result.data as Map<*, *>
        val questions = data["questions"] as List<Map<String, Any?>>
        return questions.map { map ->
            Question(
                trackId = map["trackId"] as String,
                correctTitle = map["correctTitle"] as String,
                options = (map["options"] as List<*>).filterIsInstance<String>(),
                previewUrl = map["previewUrl"] as String,
                appleUrl = map["appleUrl"] as? String
            )
        }
    }

    override suspend fun submitSoloAnswer(gameId: String, questionIndex: Int, selected: String): Int {
        val callable = functions.getHttpsCallable("submitSoloAnswer")
        val result = callable.call(
            mapOf(
                "gameId" to gameId,
                "questionIndex" to questionIndex,
                "selected" to selected
            )
        ).await()
        val data = result.data as Map<*, *>
        return (data["score"] as Number).toInt()
    }

    override fun observeMatch(matchId: String): Flow<GameMatchState> = callbackFlow {
        val registration = firestore.collection("matches").document(matchId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val myUid = auth.currentUser?.uid.orEmpty()
                    val state = snapshot.getString("state") ?: "waiting"
                    val currentQuestion = snapshot.getLong("currentQuestion")?.toInt() ?: 0
                    val players = snapshot.get("players") as? Map<*, *>
                    val myNode = players?.values?.filterIsInstance<Map<*, *>>()?.find { it["uid"] == myUid }
                    val opponentNode = players?.values?.filterIsInstance<Map<*, *>>()?.firstOrNull { it["uid"] != myUid }
                    trySend(
                        GameMatchState(
                            matchId = matchId,
                            state = state,
                            currentQuestion = currentQuestion,
                            myScore = (myNode?.get("score") as? Number)?.toInt() ?: 0,
                            opponentScore = (opponentNode?.get("score") as? Number)?.toInt() ?: 0,
                            opponentConnected = opponentNode?.get("connected") as? Boolean ?: true
                        )
                    )
                }
            }
        awaitClose { registration.remove() }
    }

    override suspend fun submitMatchAnswer(matchId: String, questionIndex: Int, selected: String) {
        functions.getHttpsCallable("submitAnswer").call(
            mapOf(
                "matchId" to matchId,
                "questionIndex" to questionIndex,
                "selected" to selected
            )
        ).await()
    }

    override suspend fun finalizeMatch(matchId: String) {
        functions.getHttpsCallable("finalizeMatch").call(mapOf("matchId" to matchId)).await()
    }

    private fun GameConfig.toMap(): Map<String, Any> = buildMap {
        put("artistId", artistId)
        put("genreFilter", genreFilter)
        era?.let { put("era", listOf(it.first, it.last)) }
        put("questionCount", questionCount)
        put("perQuestionSeconds", perQuestionSeconds)
    }
}
