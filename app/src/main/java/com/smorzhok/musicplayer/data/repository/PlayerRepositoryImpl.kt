package com.smorzhok.musicplayer.data.repository

import com.smorzhok.musicplayer.domain.model.PlaybackProgress
import com.smorzhok.musicplayer.domain.model.PlaybackState
import com.smorzhok.musicplayer.domain.model.Track
import com.smorzhok.musicplayer.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class PlayerRepositoryImpl : PlayerRepository {

    private var currentTrack: Track? = null
    private val playbackState = MutableStateFlow(PlaybackState.STOPPED)
    private val playbackProgress = MutableStateFlow(PlaybackProgress(0, 0))

    override fun play(track: Track) {
        currentTrack = track
        playbackState.value = PlaybackState.PLAYING
        playbackProgress.value = PlaybackProgress(0, track.duration)
    }

    override fun pause() {
        playbackState.value = PlaybackState.PAUSED
    }

    override fun resume() {
        playbackState.value = PlaybackState.PLAYING
    }

    override fun stop() {
        playbackState.value = PlaybackState.STOPPED
        playbackProgress.value = PlaybackProgress(0, 0)
    }

    override fun seekTo(position: Int) {
        currentTrack?.let {
            playbackProgress.value = PlaybackProgress(position, it.duration)
        }
    }

    override fun playNext() {
        //передать новый track в play()
    }

    override fun playPrevious() {
        // передать новый track в play()
    }

    override fun getCurrentTrack(): Track? = currentTrack

    override fun observePlaybackState(): Flow<PlaybackState> = playbackState

    override fun observeProgress(): Flow<PlaybackProgress> = playbackProgress
}