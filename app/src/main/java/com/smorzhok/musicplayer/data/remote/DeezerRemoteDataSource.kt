package com.smorzhok.musicplayer.data.remote

import com.smorzhok.musicplayer.data.dto.TrackDto

interface DeezerRemoteDataSource {
    suspend fun searchTracks(query: String): List<TrackDto>
    suspend fun getTrackById(id: Long): TrackDto
    suspend fun getChartTracks(index: Int): List<TrackDto>
}