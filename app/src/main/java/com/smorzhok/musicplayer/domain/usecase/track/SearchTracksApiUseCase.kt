package com.smorzhok.musicplayer.domain.usecase.track

import com.smorzhok.musicplayer.domain.repository.TrackRepository

class SearchTracksApiUseCase(private val repository: TrackRepository) {
    suspend operator fun invoke(query: String) = repository.searchTracksApi(query)
}