package com.smorzhok.musicplayer.domain.usecase.track

import com.smorzhok.musicplayer.domain.repository.TrackRepository

class GetTrackByIdUseCase(private val repository: TrackRepository) {
    suspend operator fun invoke(id: Long) = repository.getTrackById(id)
}