package com.sharkoguess.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sharkoguess.core.database.model.TrackEntity

@Dao
interface TrackDao {
    @Query("SELECT * FROM tracks WHERE artistId = :artistId")
    suspend fun getTracksForArtist(artistId: String): List<TrackEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tracks: List<TrackEntity>)
}
