package com.sharkoguess.feature.solo

import androidx.compose.animation.Crossfade
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalContext
import com.sharkoguess.core.player.PreviewPlayer
import com.sharkoguess.feature.solo.R

@Composable
fun SoloGameRoute(
    artistId: String,
    onFinished: () -> Unit,
    viewModel: SoloGameViewModel
) {
    val state by viewModel.state.collectAsState()
    SoloGameScreen(
        state = state,
        onOptionClick = viewModel::submitAnswer,
        onNext = viewModel::nextQuestion,
        onFinished = onFinished
    )
}

@Composable
fun SoloGameScreen(
    state: SoloGameUiState,
    onOptionClick: (String) -> Unit,
    onNext: () -> Unit,
    onFinished: () -> Unit
) {
    if (state.finished) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = stringResource(id = R.string.game_finished))
            Text(text = stringResource(id = R.string.score_label, state.score))
            Button(onClick = onFinished) { Text(text = stringResource(id = R.string.back_to_menu)) }
        }
        return
    }
    val question = state.questions.getOrNull(state.currentIndex)
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = stringResource(id = R.string.question_counter, state.currentIndex + 1, state.questions.size))
            val progress = if (com.sharkoguess.domain.model.SECONDS_PER_QUESTION == 0) 0f else state.remainingSeconds.toFloat() / com.sharkoguess.domain.model.SECONDS_PER_QUESTION
        CircularProgressIndicator(progress = progress)
        }
        question?.let {
            Text(text = stringResource(id = R.string.listen_and_guess))
            AudioPreview(url = it.previewUrl, enabled = !state.isLocked)
            it.options.forEach { option ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLocked,
                        onClick = { onOptionClick(option) }
                    ) { Text(text = option) }
                }
            }
            Crossfade(targetState = state.lastAnswerCorrect) { correct ->
                when (correct) {
                    true -> Text(text = stringResource(id = R.string.answer_correct), color = MaterialTheme.colorScheme.primary)
                    false -> Text(text = stringResource(id = R.string.answer_wrong), color = MaterialTheme.colorScheme.error)
                    null -> {}
                }
            }
            if (state.isLocked) {
                Button(onClick = onNext) { Text(text = stringResource(id = R.string.next_question)) }
                question.appleUrl?.let { url ->
                    Button(onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    }) {
                        Text(text = stringResource(id = R.string.open_in_apple_music))
                    }
                }
            }
        } ?: run {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun AudioPreview(url: String, enabled: Boolean, player: PreviewPlayer = hiltViewModel<PreviewPlayerViewModel>().player) {
    LaunchedEffect(url, enabled) {
        if (enabled) player.play(url) else player.pause()
    }
    DisposableEffect(Unit) {
        onDispose { player.stop() }
    }
    Text(text = stringResource(id = R.string.tap_to_listen))
}

