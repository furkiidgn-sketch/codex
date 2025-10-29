package com.sharkoguess.feature.solo

import androidx.lifecycle.ViewModel
import com.sharkoguess.core.player.PreviewPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreviewPlayerViewModel @Inject constructor(
    val player: PreviewPlayer
) : ViewModel()
