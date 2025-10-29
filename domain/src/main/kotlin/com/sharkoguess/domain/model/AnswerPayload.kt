package com.sharkoguess.domain.model

data class AnswerPayload(
    val questionIndex: Int,
    val selected: String,
    val clientTs: Long
)
