package com.smorzhok.musicplayer.data.remote

import android.util.Log
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
        val responce = apiService.getChartTracks(index).data
        Log.d("Doing", responce.toString())
        return responce
    }
}