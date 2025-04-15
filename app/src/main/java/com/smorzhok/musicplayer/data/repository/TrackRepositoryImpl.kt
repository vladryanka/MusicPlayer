package com.smorzhok.musicplayer.data.repository

import com.smorzhok.musicplayer.data.mapper.toDomain
import com.smorzhok.musicplayer.data.mapper.toEntity
import com.smorzhok.musicplayer.data.remote.DeezerRemoteDataSource
import com.smorzhok.musicplayer.data.remote.LocalMusicDataSource
import com.smorzhok.musicplayer.domain.model.Track
import com.smorzhok.musicplayer.domain.repository.TrackRepository

class TrackRepositoryImpl(
    private val localDataSource: LocalMusicDataSource,
    private val remoteDataSource: DeezerRemoteDataSource
) : TrackRepository {

    override suspend fun searchTracks(query: String): List<Track> {
        return remoteDataSource.searchTracks(query).map { it.toDomain() }
    }

    override suspend fun getDownloadedTracks(): List<Track> {
        return localDataSource.getAllTracks().map { it.toDomain() }
    }

    override suspend fun downloadTrack(track: Track) {
        localDataSource.insertTrack(track.toEntity())
    }

    override suspend fun searchDownloadedTracks(query: String): List<Track> {
        return localDataSource.searchTracks(query).map { it.toDomain() }
    }

    override suspend fun deleteDownloadedTrack(trackId: Long) {
        val track = localDataSource.getAllTracks().find { it.id == trackId }
        track?.let {
            localDataSource.deleteTrack(it.id)
        }
    }
}
