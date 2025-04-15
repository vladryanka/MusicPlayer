package com.smorzhok.musicplayer.domain.usecase.player

import com.smorzhok.musicplayer.domain.model.Track
import com.smorzhok.musicplayer.domain.repository.PlayerRepository

class PlayTrackUseCase(private val playerRepository: PlayerRepository) {
    operator fun invoke(track: Track) = playerRepository.play(track)
}