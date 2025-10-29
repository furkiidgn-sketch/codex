package com.sharkoguess.feature.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sharkoguess.feature.onboarding.R

@Composable
fun OnboardingRoute(
    onContinue: () -> Unit,
    viewModel: OnboardingViewModel
) {
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
    OnboardingScreen(
        isAuthenticated = isAuthenticated,
        onAnonClick = { viewModel.continueAnonymously(onContinue) },
        onContinue = onContinue
    )
}

@Composable
fun OnboardingScreen(
    isAuthenticated: Boolean,
    onAnonClick: () -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = null)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = stringResource(id = R.string.onboarding_title))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(id = R.string.onboarding_subtitle))
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onAnonClick) {
            Text(text = stringResource(id = R.string.sign_in_anon))
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (isAuthenticated) {
            Button(onClick = onContinue) {
                Text(text = stringResource(id = R.string.start_game))
            }
        }
    }
}
