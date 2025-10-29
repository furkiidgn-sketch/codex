package com.sharkoguess.feature.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharkoguess.domain.model.GameConfig
import com.sharkoguess.domain.model.DEFAULT_ERAS
import com.sharkoguess.domain.model.DEFAULT_GENRES
import com.sharkoguess.domain.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CategoryUiState())
    val state: StateFlow<CategoryUiState> = _state

    fun onGenreToggle(genre: String) {
        val genres = _state.value.selectedGenres.toMutableList().apply {
            if (contains(genre)) remove(genre) else add(genre)
        }
        _state.value = _state.value.copy(selectedGenres = genres)
    }

    fun onEraSelect(era: IntRange) {
        _state.value = _state.value.copy(selectedEra = era)
    }

    fun requestMatch(artistId: String, onMatchReady: (String) -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true)
            val config = buildConfig(artistId)
            val matchId = gameRepository.findOrCreateMatch(config)
            _state.value = _state.value.copy(loading = false)
            onMatchReady(matchId)
        }
    }

    fun buildConfig(artistId: String): GameConfig {
        return GameConfig(
            artistId = artistId,
            genreFilter = _state.value.selectedGenres.ifEmpty { DEFAULT_GENRES },
            era = _state.value.selectedEra,
            questionCount = com.sharkoguess.domain.model.QUESTIONS_PER_GAME,
            perQuestionSeconds = com.sharkoguess.domain.model.SECONDS_PER_QUESTION
        )
    }
}

data class CategoryUiState(
    val selectedGenres: List<String> = DEFAULT_GENRES,
    val selectedEra: IntRange? = DEFAULT_ERAS.firstOrNull(),
    val loading: Boolean = false
)
