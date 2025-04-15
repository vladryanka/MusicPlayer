package com.smorzhok.musicplayer.domain.repository

import com.smorzhok.musicplayer.domain.model.Track

interface TrackRepository {
    suspend fun searchTracks(query: String): List<Track>
    suspend fun getDownloadedTracks(): List<Track>
    suspend fun downloadTrack(track: Track)
    suspend fun searchDownloadedTracks(query: String): List<Track>
    suspend fun deleteDownloadedTrack(trackId: Long)
}