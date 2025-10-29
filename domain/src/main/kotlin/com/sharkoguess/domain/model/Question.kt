package com.sharkoguess.domain.model

data class Question(
    val trackId: String,
    val correctTitle: String,
    val options: List<String>,
    val previewUrl: String,
    val appleUrl: String? = null
)
