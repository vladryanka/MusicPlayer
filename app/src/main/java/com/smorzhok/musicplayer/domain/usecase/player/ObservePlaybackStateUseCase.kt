package com.smorzhok.musicplayer.domain.usecase.player


import com.smorzhok.musicplayer.domain.model.PlaybackState
import com.smorzhok.musicplayer.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow

class ObservePlaybackStateUseCase(private val playerRepository: PlayerRepository) {
    operator fun invoke(): Flow<PlaybackState> = playerRepository.observePlaybackState()
}