package com.sharkoguess.feature.artistpicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharkoguess.core.common.DispatchersProvider
import com.sharkoguess.data.applemusic.TokenProvider
import com.sharkoguess.domain.model.Artist
import com.sharkoguess.domain.repository.ArtistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistPickerViewModel @Inject constructor(
    private val artistRepository: ArtistRepository,
    private val dispatchersProvider: DispatchersProvider
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _searchResults = MutableStateFlow<List<Artist>>(emptyList())

    val state: StateFlow<ArtistPickerUiState> = combine(
        _query,
        _searchResults,
        artistRepository.observeRecentArtists()
    ) { query, results, recent ->
        ArtistPickerUiState(query = query, results = results, recent = recent)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ArtistPickerUiState())

    fun onQueryChange(query: String) {
        _query.value = query
        if (query.length < 2) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch(dispatchersProvider.io) {
            runCatching {
                artistRepository.searchArtists(TokenProvider.storefront, query)
            }.onSuccess { artists ->
                _searchResults.value = artists
            }
        }
    }

    fun onArtistSelected(artist: Artist) {
        viewModelScope.launch(dispatchersProvider.io) {
            artistRepository.saveRecentArtist(artist)
        }
    }
}

data class ArtistPickerUiState(
    val query: String = "",
    val results: List<Artist> = emptyList(),
    val recent: List<Artist> = emptyList()
)
