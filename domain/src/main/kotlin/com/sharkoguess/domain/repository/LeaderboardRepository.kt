package com.sharkoguess.domain.repository

import kotlinx.coroutines.flow.Flow

data class LeaderboardEntry(
    val uid: String,
    val displayName: String,
    val score: Int,
    val photoUrl: String?,
    val country: String?
)

interface LeaderboardRepository {
    fun observeLeaderboard(period: String): Flow<List<LeaderboardEntry>>
}
