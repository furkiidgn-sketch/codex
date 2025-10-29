package com.sharkoguess.domain.usecase

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CalculateScoreUseCaseTest {
    private val useCase = CalculateScoreUseCase()

    @Test
    fun scoreIncreasesWithRemainingTime() {
        val fast = useCase.execute(CalculateScoreUseCase.Input(true, remainingSeconds = 25, currentStreak = 1))
        val slow = useCase.execute(CalculateScoreUseCase.Input(true, remainingSeconds = 5, currentStreak = 1))
        assertTrue(fast > slow)
    }

    @Test
    fun incorrectAnswerReturnsZero() {
        val score = useCase.execute(CalculateScoreUseCase.Input(false, remainingSeconds = 30, currentStreak = 5))
        assertEquals(0, score)
    }
}
