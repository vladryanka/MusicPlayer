package com.smorzhok.musicplayer.data.remote

import com.smorzhok.musicplayer.data.dto.TrackDto
import com.smorzhok.musicplayer.data.dto.WrappedAlbumData

interface DeezerRemoteDataSource {
    suspend fun searchTracks(query: String): List<TrackDto>
    suspend fun getTrackById(id: Long): TrackDto
    suspend fun getChartTracks(index: Int): List<TrackDto>
    suspend fun getAlbumById(id: Long): WrappedAlbumData
}