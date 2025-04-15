package com.smorzhok.musicplayer.domain.usecase.player

import com.smorzhok.musicplayer.domain.repository.PlayerRepository

class PlayPreviousTrackUseCase(private val playerRepository: PlayerRepository) {
    operator fun invoke() = playerRepository.playPrevious()
}