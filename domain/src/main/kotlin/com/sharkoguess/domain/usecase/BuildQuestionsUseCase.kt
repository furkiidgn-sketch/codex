package com.sharkoguess.domain.usecase

import com.sharkoguess.domain.model.GameConfig
import com.sharkoguess.domain.model.Question
import com.sharkoguess.domain.model.Track
import javax.inject.Inject

class BuildQuestionsUseCase @Inject constructor() {
    fun execute(config: GameConfig, tracks: List<Track>): List<Question> {
        val eligibleTracks = tracks
            .filter { it.previewUrl != null }
            .filter { track ->
                config.era?.let { range -> track.releaseDate?.year?.let(range::contains) ?: false } ?: true
            }
            .filter { track ->
                if (config.genreFilter.isEmpty()) return@filter true
                val lowerGenres = track.genres.map { it.lowercase() }
                config.genreFilter.any { genre -> genre.lowercase() in lowerGenres }
            }
            .distinctBy { it.id }

        val questionPool = eligibleTracks.shuffled().take(config.questionCount)
        val remaining = eligibleTracks - questionPool.toSet()

        return questionPool.mapIndexed { index, track ->
            val incorrectOptions = remaining
                .filter { it.id != track.id }
                .filter { it.artistName == track.artistName }
                .map { it.name }
                .shuffled()
                .take(2)
                .ifEmpty {
                    (eligibleTracks - track).shuffled().take(2).map { it.name }
                }
            val options = (incorrectOptions + track.name).shuffled()
            Question(
                trackId = track.id,
                correctTitle = track.name,
                options = options,
                previewUrl = requireNotNull(track.previewUrl),
                appleUrl = track.appleUrl
            )
        }
    }
}
