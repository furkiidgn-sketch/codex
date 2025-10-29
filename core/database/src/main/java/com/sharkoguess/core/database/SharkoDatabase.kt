package com.sharkoguess.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sharkoguess.core.database.dao.ArtistDao
import com.sharkoguess.core.database.dao.TrackDao
import com.sharkoguess.core.database.model.ArtistEntity
import com.sharkoguess.core.database.model.TrackEntity

@Database(entities = [ArtistEntity::class, TrackEntity::class], version = 1)
abstract class SharkoDatabase : RoomDatabase() {
    abstract fun artistDao(): ArtistDao
    abstract fun trackDao(): TrackDao
}
