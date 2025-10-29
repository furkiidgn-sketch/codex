package com.sharkoguess.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey val id: String,
    val artistId: String,
    val name: String,
    val artistName: String,
    val previewUrl: String?,
    val releaseDate: String?,
    val genres: String,
    val appleUrl: String?
)
