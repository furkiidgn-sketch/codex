package com.sharkoguess.domain.usecase

import com.sharkoguess.domain.model.ScoreSummary
import kotlin.math.roundToInt
import javax.inject.Inject

class CalculateScoreUseCase @Inject constructor() {
    data class Input(
        val isCorrect: Boolean,
        val remainingSeconds: Int,
        val currentStreak: Int
    )

    fun execute(input: Input): Int {
        if (!input.isCorrect) return 0
        val base = 500
        val timeBonus = (500 * (input.remainingSeconds / 30.0)).roundToInt()
        val streakBonus = 100 * input.currentStreak
        return base + timeBonus + streakBonus
    }

    fun summarize(scores: List<Int>, correctAnswers: Int, streakMax: Int): ScoreSummary {
        return ScoreSummary(total = scores.sum(), correctCount = correctAnswers, streakMax = streakMax)
    }
}
