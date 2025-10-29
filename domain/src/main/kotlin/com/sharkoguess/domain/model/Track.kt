package com.sharkoguess.domain.model

import java.time.LocalDate

data class Track(
    val id: String,
    val name: String,
    val artistName: String,
    val previewUrl: String?,
    val releaseDate: LocalDate?,
    val genres: List<String>,
    val appleUrl: String?
)
