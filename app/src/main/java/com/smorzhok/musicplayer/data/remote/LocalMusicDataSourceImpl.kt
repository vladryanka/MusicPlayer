package com.smorzhok.musicplayer.data.remote

import com.smorzhok.musicplayer.data.database.DownloadedTrackDao
import com.smorzhok.musicplayer.data.database.DownloadedTrackEntity

class LocalMusicDataSourceImpl(
    private val dao: DownloadedTrackDao
) : LocalMusicDataSource {

    override suspend fun getAllTracks(): List<DownloadedTrackEntity> {
        return dao.getAllTracks()
    }

    override suspend fun insertTrack(track: DownloadedTrackEntity) {
        dao.insertTrack(track)
    }

    override suspend fun deleteTrack(trackId: Long) {
        val existingTracks = dao.getAllTracks()
        existingTracks.find { it.id == trackId }?.let {
            dao.deleteTrack(it)
        }
    }

    override suspend fun searchTracks(query: String): List<DownloadedTrackEntity> {
        return dao.searchTracks(query)
    }
}