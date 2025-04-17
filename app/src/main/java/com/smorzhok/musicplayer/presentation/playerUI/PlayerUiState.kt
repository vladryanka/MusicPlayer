package com.smorzhok.musicplayer.presentation.playerUI

import com.smorzhok.musicplayer.domain.model.PlaybackProgress
import com.smorzhok.musicplayer.domain.model.PlaybackState
import com.smorzhok.musicplayer.domain.model.Track

data class PlayerUiState(
    val track: Track? = null,
    val playbackState: PlaybackState = PlaybackState.STOPPED,
    val progress: PlaybackProgress = PlaybackProgress(0, 0)
)
