package com.smorzhok.musicplayer.domain.usecase.track

import com.smorzhok.musicplayer.domain.repository.TrackRepository

class GetDownloadedTracksUseCase(private val repository: TrackRepository) {
    suspend operator fun invoke() = repository.getDownloadedTracks()
}