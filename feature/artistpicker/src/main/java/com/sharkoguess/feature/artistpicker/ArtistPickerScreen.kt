package com.sharkoguess.feature.artistpicker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sharkoguess.domain.model.Artist
import com.sharkoguess.feature.artistpicker.R

@Composable
fun ArtistPickerRoute(
    onArtistSelected: (String) -> Unit,
    onLeaderboardClick: () -> Unit,
    viewModel: ArtistPickerViewModel
) {
    val state by viewModel.state.collectAsState()
    ArtistPickerScreen(
        state = state,
        onQueryChange = viewModel::onQueryChange,
        onArtistTap = {
            viewModel.onArtistSelected(it)
            onArtistSelected(it.id)
        },
        onLeaderboardClick = onLeaderboardClick
    )
}

@Composable
fun ArtistPickerScreen(
    state: ArtistPickerUiState,
    onQueryChange: (String) -> Unit,
    onArtistTap: (Artist) -> Unit,
    onLeaderboardClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = state.query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.search_hint)) }
        )
        Button(onClick = onLeaderboardClick, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Text(text = stringResource(id = R.string.leaderboard_shortcut))
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(state.results.ifEmpty { state.recent }) { artist ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onArtistTap(artist) }
                        .padding(vertical = 12.dp)
                ) {
                    Text(text = artist.name)
                }
            }
        }
    }
}
