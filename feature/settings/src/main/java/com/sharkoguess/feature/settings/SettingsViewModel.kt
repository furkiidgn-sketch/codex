package com.sharkoguess.feature.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state

    fun onLanguageChange(language: String) {
        _state.value = _state.value.copy(language = language)
    }

    fun onSoundToggle(enabled: Boolean) {
        _state.value = _state.value.copy(soundEnabled = enabled)
    }
}

data class SettingsUiState(
    val language: String = "tr",
    val soundEnabled: Boolean = true
)
