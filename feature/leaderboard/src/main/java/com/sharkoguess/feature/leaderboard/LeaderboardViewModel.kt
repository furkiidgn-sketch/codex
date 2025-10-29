package com.sharkoguess.feature.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharkoguess.domain.repository.LeaderboardEntry
import com.sharkoguess.domain.repository.LeaderboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val repository: LeaderboardRepository
) : ViewModel() {

    private val _period = MutableStateFlow("alltime")
    val period: StateFlow<String> = _period

    val entries: StateFlow<List<LeaderboardEntry>> = _period
        .flatMapLatest { repository.observeLeaderboard(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun updatePeriod(period: String) {
        _period.value = period
    }
}
