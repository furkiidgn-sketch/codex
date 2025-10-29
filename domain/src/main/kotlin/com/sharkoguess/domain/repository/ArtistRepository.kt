package com.sharkoguess.domain.repository

import com.sharkoguess.domain.model.Artist
import kotlinx.coroutines.flow.Flow

interface ArtistRepository {
    suspend fun searchArtists(storefront: String, term: String): List<Artist>
    suspend fun getArtistTopTracks(storefront: String, artistId: String): List<com.sharkoguess.domain.model.Track>
    fun observeRecentArtists(): Flow<List<Artist>>
    suspend fun saveRecentArtist(artist: Artist)
}
