package com.smorzhok.musicplayer.data.remote

import com.smorzhok.musicplayer.data.dto.TrackDto

class DeezerRemoteDataSourceImpl(
    private val apiService: DeezerApiService
) : DeezerRemoteDataSource {

    override suspend fun searchTracks(query: String): List<TrackDto> {
        return apiService.searchTracks(query).data
    }

    override suspend fun getTrackById(id: Long): TrackDto {
        return apiService.getTrackById(id)
    }

    override suspend fun getChartTracks(index: Int): List<TrackDto> {
        return apiService.getChartTracks(index).tracks.data
    }
}