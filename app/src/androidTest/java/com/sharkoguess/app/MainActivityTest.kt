package com.sharkoguess.app

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun showsOnboardingTitle() {
        val expected = composeTestRule.activity.getString(R.string.onboarding_title)
        composeTestRule.onNodeWithText(expected).assertExists()
    }
}
