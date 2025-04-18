package com.smorzhok.musicplayer.domain.usecase.player

import com.smorzhok.musicplayer.domain.model.Track
import com.smorzhok.musicplayer.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveCurrentTrackUseCase(
    private val playerRepository: PlayerRepository
) {
    operator fun invoke(): StateFlow<Track?> = playerRepository.observeCurrentTrack()
}