package com.smorzhok.musicplayer.domain.repository

import com.smorzhok.musicplayer.domain.model.PlaybackProgress
import com.smorzhok.musicplayer.domain.model.PlaybackState
import com.smorzhok.musicplayer.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface PlayerRepository {
    fun play(track: Track)
    fun pause()
    fun resume()
    fun getTrackList(): List<Track>
    fun stop()
    fun seekTo(position: Int)
    fun playNext()
    fun playPrevious()
    fun getCurrentTrack(): Track?
    fun getPlaybackError():SharedFlow<String>
    fun observePlaybackState(): Flow<PlaybackState>
    fun observeProgress(): Flow<PlaybackProgress>
    fun setTrackList(tracks: List<Track>, selectedIndex: Int)
}