package com.sharkoguess.core.database.di

import android.content.Context
import androidx.room.Room
import com.sharkoguess.core.database.SharkoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SharkoDatabase =
        Room.databaseBuilder(context, SharkoDatabase::class.java, "sharkoguess.db").build()

    @Provides
    fun provideArtistDao(db: SharkoDatabase) = db.artistDao()

    @Provides
    fun provideTrackDao(db: SharkoDatabase) = db.trackDao()
}
