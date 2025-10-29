package com.sharkoguess.data.applemusic.di

import com.sharkoguess.data.applemusic.AppleMusicRepository
import com.sharkoguess.domain.repository.ArtistRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppleMusicModule {
    @Binds
    @Singleton
    abstract fun bindArtistRepository(impl: AppleMusicRepository): ArtistRepository
}
