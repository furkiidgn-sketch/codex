package com.sharkoguess.data.applemusic

import com.sharkoguess.core.database.dao.ArtistDao
import com.sharkoguess.core.database.dao.TrackDao
import com.sharkoguess.core.database.model.ArtistEntity
import com.sharkoguess.core.database.model.TrackEntity
import com.sharkoguess.core.network.AppleMusicService
import com.sharkoguess.domain.model.Artist
import com.sharkoguess.domain.model.Track
import com.sharkoguess.domain.repository.ArtistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppleMusicRepository @Inject constructor(
    private val service: AppleMusicService,
    private val artistDao: ArtistDao,
    private val trackDao: TrackDao
) : ArtistRepository {

    override suspend fun searchArtists(storefront: String, term: String): List<Artist> {
        val response = service.searchArtists("Bearer ${TokenProvider.developerToken}", storefront = storefront, term = term)
        return response.results.artists?.data?.map { data ->
            Artist(id = data.id, name = data.attributes.name, artworkUrl = data.attributes.artwork?.url)
        } ?: emptyList()
    }

    override suspend fun getArtistTopTracks(storefront: String, artistId: String): List<Track> {
        val response = service.getArtistTopSongs("Bearer ${TokenProvider.developerToken}", storefront = storefront, artistId = artistId)
        val tracks = response.data.mapNotNull { data ->
            val preview = data.attributes.previews.firstOrNull()?.url ?: return@mapNotNull null
            Track(
                id = data.id,
                name = data.attributes.name,
                artistName = data.attributes.artistName,
                previewUrl = preview,
                releaseDate = data.attributes.releaseDate?.let { LocalDate.parse(it, DateTimeFormatter.ISO_DATE) },
                genres = data.attributes.genreNames,
                appleUrl = data.attributes.url
            )
        }
        trackDao.insertAll(
            tracks.map {
                TrackEntity(
                    id = it.id,
                    artistId = artistId,
                    name = it.name,
                    artistName = it.artistName,
                    previewUrl = it.previewUrl,
                    releaseDate = it.releaseDate?.toString(),
                    genres = it.genres.joinToString(","),
                    appleUrl = it.appleUrl
                )
            }
        )
        return tracks
    }

    override fun observeRecentArtists(): Flow<List<Artist>> = artistDao.observeRecent().map { entities ->
        entities.map { Artist(id = it.id, name = it.name, artworkUrl = it.artworkUrl) }
    }

    override suspend fun saveRecentArtist(artist: Artist) {
        artistDao.insert(ArtistEntity(id = artist.id, name = artist.name, artworkUrl = artist.artworkUrl))
    }
}
