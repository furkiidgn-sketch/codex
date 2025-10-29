package com.sharkoguess.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sharkoguess.feature.settings.R

@Composable
fun SettingsRoute(
    onBack: () -> Unit,
    viewModel: SettingsViewModel
) {
    val state by viewModel.state.collectAsState()
    SettingsScreen(state = state, onLanguageChange = viewModel::onLanguageChange, onSoundToggle = viewModel::onSoundToggle, onBack = onBack)
}

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onLanguageChange: (String) -> Unit,
    onSoundToggle: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = stringResource(id = R.string.settings_title))
        Text(text = stringResource(id = R.string.language_current, state.language.uppercase()))
        Button(onClick = { onLanguageChange(if (state.language == "tr") "en" else "tr") }) {
            Text(text = stringResource(id = R.string.toggle_language))
        }
        Text(text = stringResource(id = R.string.sound))
        Switch(checked = state.soundEnabled, onCheckedChange = onSoundToggle)
        Button(onClick = onBack) { Text(text = stringResource(id = R.string.back)) }
    }
}
