package com.smorzhok.musicplayer.domain.usecase.track

import com.smorzhok.musicplayer.domain.model.Track
import com.smorzhok.musicplayer.domain.repository.TrackRepository

class DownloadTrackUseCase(private val trackRepository: TrackRepository) {
    suspend operator fun invoke(track: Track) = trackRepository.downloadTrack(track)
}