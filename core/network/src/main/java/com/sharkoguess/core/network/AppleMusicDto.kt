package com.sharkoguess.core.network

import com.squareup.moshi.Json

// region DTOs

data class AppleSearchResponse(
    val results: AppleSearchResults = AppleSearchResults()
)

data class AppleSearchResults(
    val artists: AppleArtists? = null
)

data class AppleArtists(
    val data: List<AppleArtistData> = emptyList()
)

data class AppleArtistData(
    val id: String,
    val attributes: AppleArtistAttributes
)

data class AppleArtistAttributes(
    val name: String,
    @Json(name = "artwork") val artwork: AppleArtwork? = null
)

data class AppleArtwork(
    @Json(name = "url") val url: String?
)

data class AppleSongsResponse(
    val data: List<AppleSongData> = emptyList()
)

data class AppleSongData(
    val id: String,
    val attributes: AppleSongAttributes
)

data class AppleSongAttributes(
    val name: String,
    @Json(name = "artistName") val artistName: String,
    @Json(name = "url") val url: String?,
    @Json(name = "releaseDate") val releaseDate: String?,
    @Json(name = "genreNames") val genreNames: List<String> = emptyList(),
    @Json(name = "previews") val previews: List<ApplePreview> = emptyList()
)

data class ApplePreview(
    val url: String
)
// endregion
