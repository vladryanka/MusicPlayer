package com.smorzhok.musicplayer.domain.usecase.player

import com.smorzhok.musicplayer.domain.model.PlaybackProgress
import com.smorzhok.musicplayer.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow

class ObserveProgressUseCase (private val playerRepository: PlayerRepository) {
    operator fun invoke(): Flow<PlaybackProgress> = playerRepository.observeProgress()
}