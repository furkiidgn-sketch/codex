package com.sharkoguess.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isAuthenticated: Flow<Boolean>
    suspend fun signInAnonymously(): Unit
    suspend fun signInWithGoogle(idToken: String)
    suspend fun signOut()
}
