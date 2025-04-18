package com.smorzhok.musicplayer.domain.usecase.player

import com.smorzhok.musicplayer.domain.repository.PlayerRepository

class GetPlaybackError(private val repository: PlayerRepository) {
    operator fun invoke() = repository.getPlaybackError()
}