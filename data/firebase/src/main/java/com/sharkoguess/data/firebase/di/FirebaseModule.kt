package com.sharkoguess.data.firebase.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.sharkoguess.data.firebase.FirebaseAuthRepository
import com.sharkoguess.data.firebase.FirebaseGameRepository
import com.sharkoguess.data.firebase.FirebaseLeaderboardRepository
import com.sharkoguess.domain.repository.AuthRepository
import com.sharkoguess.domain.repository.GameRepository
import com.sharkoguess.domain.repository.LeaderboardRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseBindModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: FirebaseAuthRepository): AuthRepository

    @Binds
    @Singleton
    abstract fun bindGameRepository(impl: FirebaseGameRepository): GameRepository

    @Binds
    @Singleton
    abstract fun bindLeaderboardRepository(impl: FirebaseLeaderboardRepository): LeaderboardRepository
}

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    @Singleton
    fun provideAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFunctions(): FirebaseFunctions = FirebaseFunctions.getInstance()
}
