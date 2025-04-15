package com.smorzhok.musicplayer.domain.usecase.track

import com.smorzhok.musicplayer.domain.model.Track
import com.smorzhok.musicplayer.domain.repository.TrackRepository

class SearchDownloadedTracksUseCase(private val trackRepository: TrackRepository) {
    suspend operator fun invoke(query: String): List<Track> =
        trackRepository.searchDownloadedTracks(query)
}