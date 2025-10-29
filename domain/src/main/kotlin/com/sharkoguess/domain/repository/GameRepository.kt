package com.sharkoguess.domain.repository

import com.sharkoguess.domain.model.GameConfig
import com.sharkoguess.domain.model.Question
import com.sharkoguess.domain.model.ScoreSummary
import kotlinx.coroutines.flow.Flow

suspend fun findOrCreateMatch(config: GameConfig): String
interface GameRepository {
    suspend fun generateQuestions(config: GameConfig): List<Question>
    suspend fun submitSoloAnswer(gameId: String, questionIndex: Int, selected: String): Int
    fun observeMatch(matchId: String): Flow<GameMatchState>
    suspend fun submitMatchAnswer(matchId: String, questionIndex: Int, selected: String)
    suspend fun finalizeMatch(matchId: String)
}

data class GameMatchState(
    val matchId: String,
    val state: String,
    val currentQuestion: Int,
    val myScore: Int,
    val opponentScore: Int,
    val opponentConnected: Boolean
)
