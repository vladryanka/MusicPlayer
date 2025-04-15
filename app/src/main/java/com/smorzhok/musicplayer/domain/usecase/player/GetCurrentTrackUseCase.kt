package com.smorzhok.musicplayer.domain.usecase.player

import com.smorzhok.musicplayer.domain.repository.PlayerRepository
import com.smorzhok.musicplayer.domain.model.Track

class GetCurrentTrackUseCase(private val playerRepository: PlayerRepository) {
    operator fun invoke(): Track? = playerRepository.getCurrentTrack()
}