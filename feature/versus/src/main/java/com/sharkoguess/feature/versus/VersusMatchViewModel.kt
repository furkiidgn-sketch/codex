package com.sharkoguess.feature.versus

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharkoguess.domain.repository.GameMatchState
import com.sharkoguess.domain.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VersusMatchViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val matchId: String = savedStateHandle.get<String>("matchId").orEmpty()

    val state: StateFlow<GameMatchState> = gameRepository.observeMatch(matchId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GameMatchState(matchId, "waiting", 0, 0, 0, true))

    fun submitAnswer(questionIndex: Int, option: String) {
        viewModelScope.launch {
            gameRepository.submitMatchAnswer(matchId, questionIndex, option)
        }
    }

    fun finalize() {
        viewModelScope.launch { gameRepository.finalizeMatch(matchId) }
    }
}
