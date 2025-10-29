package com.sharkoguess.feature.leaderboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sharkoguess.domain.repository.LeaderboardEntry
import com.sharkoguess.feature.leaderboard.R

@Composable
fun LeaderboardRoute(
    onBack: () -> Unit,
    viewModel: LeaderboardViewModel
) {
    val period by viewModel.period.collectAsState()
    val entries by viewModel.entries.collectAsState()
    LeaderboardScreen(period = period, entries = entries, onPeriodChange = viewModel::updatePeriod, onBack = onBack)
}

@Composable
fun LeaderboardScreen(
    period: String,
    entries: List<LeaderboardEntry>,
    onPeriodChange: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = stringResource(id = R.string.leaderboard_title))
        RowSelector(period = period, onPeriodChange = onPeriodChange)
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(entries) { entry ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = entry.displayName)
                        Text(text = stringResource(id = R.string.score_format, entry.score))
                    }
                }
            }
        }
        Button(onClick = onBack) { Text(text = stringResource(id = R.string.back)) }
    }
}

@Composable
fun RowSelector(period: String, onPeriodChange: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Button(onClick = { onPeriodChange("alltime") }, enabled = period != "alltime") {
            Text(text = stringResource(id = R.string.period_all))
        }
        Button(onClick = { onPeriodChange("weekly") }, enabled = period != "weekly") {
            Text(text = stringResource(id = R.string.period_weekly))
        }
    }
}
