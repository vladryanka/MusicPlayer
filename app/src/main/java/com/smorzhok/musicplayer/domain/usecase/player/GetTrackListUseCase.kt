package com.smorzhok.musicplayer.domain.usecase.player

import com.smorzhok.musicplayer.domain.repository.PlayerRepository

class GetTrackListUseCase(private val repository: PlayerRepository) {
    operator fun invoke() = repository.getTrackList()
}