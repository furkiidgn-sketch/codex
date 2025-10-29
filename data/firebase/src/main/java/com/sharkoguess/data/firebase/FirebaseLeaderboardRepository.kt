package com.sharkoguess.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.sharkoguess.domain.repository.LeaderboardEntry
import com.sharkoguess.domain.repository.LeaderboardRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseLeaderboardRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : LeaderboardRepository {

    override fun observeLeaderboard(period: String): Flow<List<LeaderboardEntry>> = callbackFlow {
        val listener = firestore.collection("leaderboard").document(period).collection("entries")
            .orderBy("score", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(100)
            .addSnapshotListener { snapshot, _ ->
                val entries = snapshot?.documents?.map { doc ->
                    LeaderboardEntry(
                        uid = doc.id,
                        displayName = doc.getString("displayName") ?: "",
                        score = (doc.getLong("score") ?: 0L).toInt(),
                        photoUrl = doc.getString("photoUrl"),
                        country = doc.getString("country")
                    )
                } ?: emptyList()
                trySend(entries)
            }
        awaitClose { listener.remove() }
    }
}
