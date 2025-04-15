package com.smorzhok.musicplayer.domain.repository

import com.smorzhok.musicplayer.data.dto.TrackDto
import com.smorzhok.musicplayer.domain.model.Track

interface TrackRepository {
    suspend fun searchTracksApi(query: String): List<Track>
    suspend fun getDownloadedTracks(): List<Track>
    suspend fun getTrackById(id: Long): TrackDto
    suspend fun getChartTracks(): List<TrackDto>
}