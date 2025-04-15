package com.smorzhok.musicplayer.data.remote

import com.smorzhok.musicplayer.data.dto.TrackDto


interface LocalMusicDataSource {
    suspend fun getAllTracks(): List<TrackDto>
}