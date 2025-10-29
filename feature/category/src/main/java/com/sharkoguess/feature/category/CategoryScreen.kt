package com.sharkoguess.feature.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import com.sharkoguess.feature.category.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sharkoguess.domain.model.DEFAULT_ERAS
import com.sharkoguess.domain.model.DEFAULT_GENRES

@Composable
fun CategoryRoute(
    artistId: String,
    onStartSolo: () -> Unit,
    onStartVersus: (String) -> Unit,
    viewModel: CategoryViewModel
) {
    val state by viewModel.state.collectAsState()
    CategoryScreen(
        state = state,
        onGenreToggle = viewModel::onGenreToggle,
        onEraSelect = viewModel::onEraSelect,
        onPlaySolo = {
            // config consumed in Solo screen via saved state handle or remote call
            onStartSolo()
        },
        onPlayVersus = {
            viewModel.requestMatch(artistId) { matchId -> onStartVersus(matchId) }
        }
    )
}

@Composable
fun CategoryScreen(
    state: CategoryUiState,
    onGenreToggle: (String) -> Unit,
    onEraSelect: (IntRange) -> Unit,
    onPlaySolo: () -> Unit,
    onPlayVersus: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = stringResource(id = R.string.title_genre))
        DEFAULT_GENRES.forEach { genre ->
            Card(modifier = Modifier.fillMaxWidth().clickable { onGenreToggle(genre) }) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = genre)
                    if (state.selectedGenres.contains(genre)) {
                        Text(text = "✓")
                    }
                }
            }
        }
        Text(text = stringResource(id = R.string.title_era))
        DEFAULT_ERAS.forEach { era ->
            Card(modifier = Modifier.fillMaxWidth().clickable { onEraSelect(era) }) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "${era.first}'-${era.last}'")
                    if (state.selectedEra == era) {
                        Text(text = "✓")
                    }
                }
            }
        }
        Button(onClick = onPlaySolo, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(id = R.string.action_solo))
        }
        Button(onClick = onPlayVersus, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(id = R.string.action_versus))
        }
    }
}
