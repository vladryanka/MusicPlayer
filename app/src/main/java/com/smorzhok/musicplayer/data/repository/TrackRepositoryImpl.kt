package com.smorzhok.musicplayer.data.repository

import com.smorzhok.musicplayer.data.dto.TrackDto
import com.smorzhok.musicplayer.data.mapper.toDomain
import com.smorzhok.musicplayer.data.remote.DeezerRemoteDataSource
import com.smorzhok.musicplayer.data.remote.LocalMusicDataSource
import com.smorzhok.musicplayer.domain.model.Track
import com.smorzhok.musicplayer.domain.repository.TrackRepository

class TrackRepositoryImpl(
    private val localDataSource: LocalMusicDataSource,
    private val remoteDataSource: DeezerRemoteDataSource
) : TrackRepository {
    private var index = 0

    override suspend fun searchTracksApi(query: String): List<Track> {
        return remoteDataSource.searchTracks(query).map { it.toDomain() }
    }
    override suspend fun getTrackById(id: Long): TrackDto{
        return remoteDataSource.getTrackById(id)
    }

    override suspend fun getChartTracks(): List<Track> {
        val result = remoteDataSource.getChartTracks(index)
        index += 8
        return result.map { it.toDomain() }
    }

    override suspend fun getDownloadedTracks(): List<Track> {
        return localDataSource.getAllTracks().map { it.toDomain() }
    }
}
