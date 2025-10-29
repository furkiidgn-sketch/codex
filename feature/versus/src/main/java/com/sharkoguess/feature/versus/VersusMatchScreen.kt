package com.sharkoguess.feature.versus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sharkoguess.feature.versus.R

@Composable
fun VersusMatchRoute(
    matchId: String,
    onFinished: () -> Unit,
    viewModel: VersusMatchViewModel
) {
    val state by viewModel.state.collectAsState()
    VersusMatchScreen(state = state, onFinalize = {
        viewModel.finalize()
        onFinished()
    })
}

@Composable
fun VersusMatchScreen(
    state: com.sharkoguess.domain.repository.GameMatchState,
    onFinalize: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = stringResource(id = R.string.match_state, state.state))
        Text(text = stringResource(id = R.string.score_you, state.myScore))
        Text(text = stringResource(id = R.string.score_opponent, state.opponentScore))
        Text(text = if (state.opponentConnected) stringResource(id = R.string.opponent_connected) else stringResource(id = R.string.opponent_disconnected))
        Button(onClick = onFinalize) { Text(text = stringResource(id = R.string.leave_match)) }
    }
}
