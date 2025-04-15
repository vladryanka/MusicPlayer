package com.smorzhok.musicplayer.domain.usecase.player

import com.smorzhok.musicplayer.domain.repository.PlayerRepository

class SeekToUseCase(private val playerRepository: PlayerRepository) {
    operator fun invoke(position: Int) = playerRepository.seekTo(position)
}