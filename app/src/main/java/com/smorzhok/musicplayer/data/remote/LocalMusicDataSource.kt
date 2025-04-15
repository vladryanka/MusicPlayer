package com.smorzhok.musicplayer.data.remote

import com.smorzhok.musicplayer.data.database.DownloadedTrackEntity

interface LocalMusicDataSource {
    suspend fun getAllTracks(): List<DownloadedTrackEntity>
    suspend fun insertTrack(track: DownloadedTrackEntity)
    suspend fun deleteTrack(trackId: Long)
    suspend fun searchTracks(query: String): List<DownloadedTrackEntity>
}