package com.smorzhok.musicplayer.domain.usecase.track

import com.smorzhok.musicplayer.domain.repository.TrackRepository

class DeleteDownloadedTrackUseCase(private val trackRepository: TrackRepository) {
    suspend operator fun invoke(trackId: Long) = trackRepository.deleteDownloadedTrack(trackId)
}