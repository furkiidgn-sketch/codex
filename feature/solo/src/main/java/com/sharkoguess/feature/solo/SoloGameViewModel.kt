package com.sharkoguess.feature.solo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.sharkoguess.core.common.DispatchersProvider
import com.sharkoguess.data.applemusic.TokenProvider
import com.sharkoguess.domain.model.GameConfig
import com.sharkoguess.domain.model.Question
import com.sharkoguess.domain.model.QUESTIONS_PER_GAME
import com.sharkoguess.domain.model.SECONDS_PER_QUESTION
import com.sharkoguess.domain.repository.ArtistRepository
import com.sharkoguess.domain.repository.GameRepository
import com.sharkoguess.domain.usecase.BuildQuestionsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SoloGameViewModel @Inject constructor(
    private val artistRepository: ArtistRepository,
    private val gameRepository: GameRepository,
    private val buildQuestions: BuildQuestionsUseCase,
    private val dispatchersProvider: DispatchersProvider,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val artistId: String = savedStateHandle.get<String>("artistId").orEmpty()

    private val _state = MutableStateFlow(SoloGameUiState())
    val state: StateFlow<SoloGameUiState> = _state

    private var timerJob: Job? = null

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        viewModelScope.launch(dispatchersProvider.io) {
            val tracks = artistRepository.getArtistTopTracks(TokenProvider.storefront, artistId)
            val config = GameConfig(artistId = artistId)
            val questions = buildQuestions.execute(config, tracks).take(QUESTIONS_PER_GAME)
            _state.value = _state.value.copy(questions = questions, currentIndex = 0, remainingSeconds = SECONDS_PER_QUESTION)
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch(dispatchersProvider.default) {
            while (_state.value.remainingSeconds > 0) {
                delay(1_000)
                _state.value = _state.value.copy(remainingSeconds = _state.value.remainingSeconds - 1)
            }
            lockAnswer(null)
        }
    }

    fun submitAnswer(option: String) {
        if (_state.value.isLocked) return
        lockAnswer(option)
    }

    private fun lockAnswer(option: String?) {
        timerJob?.cancel()
        val currentQuestion = _state.value.questions.getOrNull(_state.value.currentIndex)
        val isCorrect = option != null && option == currentQuestion?.correctTitle
        viewModelScope.launch(dispatchersProvider.io) {
            if (currentQuestion != null && option != null) {
                runCatching {
                    gameRepository.submitSoloAnswer(
                        gameId = "solo-$artistId",
                        questionIndex = _state.value.currentIndex,
                        selected = option
                    )
                }.onSuccess { scoreIncrement ->
                    _state.value = _state.value.copy(score = _state.value.score + scoreIncrement)
                }
            }
            _state.value = _state.value.copy(isLocked = true, lastAnswerCorrect = isCorrect)
        }
    }

    fun nextQuestion() {
        val nextIndex = _state.value.currentIndex + 1
        if (nextIndex >= _state.value.questions.size) {
            _state.value = _state.value.copy(finished = true)
            return
        }
        _state.value = _state.value.copy(
            currentIndex = nextIndex,
            remainingSeconds = SECONDS_PER_QUESTION,
            isLocked = false,
            lastAnswerCorrect = null
        )
        startTimer()
    }
}

data class SoloGameUiState(
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val remainingSeconds: Int = SECONDS_PER_QUESTION,
    val isLocked: Boolean = false,
    val lastAnswerCorrect: Boolean? = null,
    val score: Int = 0,
    val finished: Boolean = false
)
