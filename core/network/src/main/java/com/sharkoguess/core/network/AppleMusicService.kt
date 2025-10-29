package com.sharkoguess.core.network

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface AppleMusicService {
    @GET("v1/catalog/{storefront}/search")
    suspend fun searchArtists(
        @Header("Authorization") authorization: String,
        @Path("storefront") storefront: String,
        @Query("types") types: String = "artists",
        @Query("term") term: String
    ): AppleSearchResponse

    @GET("v1/catalog/{storefront}/artists/{id}/view/top-songs")
    suspend fun getArtistTopSongs(
        @Header("Authorization") authorization: String,
        @Path("storefront") storefront: String,
        @Path("id") artistId: String
    ): AppleSongsResponse
}
