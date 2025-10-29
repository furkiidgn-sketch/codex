package com.sharkoguess.domain.model

import kotlin.ranges.IntRange

data class GameConfig(
    val artistId: String,
    val genreFilter: List<String> = emptyList(),
    val era: IntRange? = null,
    val questionCount: Int = QUESTIONS_PER_GAME,
    val perQuestionSeconds: Int = SECONDS_PER_QUESTION
)
