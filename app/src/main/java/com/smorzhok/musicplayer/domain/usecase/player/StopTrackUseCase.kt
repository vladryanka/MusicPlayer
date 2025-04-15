package com.smorzhok.musicplayer.domain.usecase.player

import com.smorzhok.musicplayer.domain.repository.PlayerRepository

class StopTrackUseCase(private val playerRepository: PlayerRepository) {
    operator fun invoke() = playerRepository.stop()
}