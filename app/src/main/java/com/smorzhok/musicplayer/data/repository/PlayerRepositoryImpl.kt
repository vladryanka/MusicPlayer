package com.smorzhok.musicplayer.data.repository

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.smorzhok.musicplayer.domain.model.PlaybackProgress
import com.smorzhok.musicplayer.domain.model.PlaybackState
import com.smorzhok.musicplayer.domain.model.Track
import com.smorzhok.musicplayer.domain.repository.PlayerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerRepositoryImpl(private val context: Context) : PlayerRepository {

    private val exoPlayer = ExoPlayer.Builder(context).build()

    private var currentTrack: Track? = null
    private val playbackState = MutableStateFlow(PlaybackState.STOPPED)
    private val playbackProgress = MutableStateFlow(PlaybackProgress(0, 0))
    private val progressFlow = MutableStateFlow(PlaybackProgress(0, 30))

    init {
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                delay(1000)
                withContext(Dispatchers.Main) {
                    if (exoPlayer.isPlaying) {
                        val currentPosition = (exoPlayer.currentPosition / 1000).toInt()
                        val duration = (exoPlayer.duration / 1000).toInt()
                        progressFlow.value = PlaybackProgress(currentPosition, duration)
                    }
                }
            }
        }
    }

    private val trackList = mutableListOf<Track>()

    override fun setTrackList(tracks: List<Track>, selectedIndex: Int) {
        trackList.clear()
        trackList.addAll(tracks)
        currentTrack = trackList[selectedIndex]
    }

    override fun getTrackList(): List<Track> = trackList

    override fun play(track: Track) {
        currentTrack = track
        val mediaItem = MediaItem.fromUri(track.previewUrl)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()

        playbackState.value = PlaybackState.PLAYING
        playbackProgress.value = PlaybackProgress(0, track.duration)
    }

    override fun pause() {
        exoPlayer.pause()
        playbackState.value = PlaybackState.PAUSED
    }

    override fun resume() {
        exoPlayer.play()
        playbackState.value = PlaybackState.PLAYING
    }

    override fun stop() {
        exoPlayer.stop()
        playbackState.value = PlaybackState.STOPPED
        playbackProgress.value = PlaybackProgress(0, 0)
    }

    override fun seekTo(position: Int) {
        currentTrack?.let {
            playbackProgress.value = PlaybackProgress(position, it.duration)
            exoPlayer.seekTo(position * 1000L)
        }
    }

    override fun playNext() {
        val index = trackList.indexOf(currentTrack)
        if (index!=trackList.size-1)
            currentTrack = trackList[index+1]
        if (index + 1 < trackList.size) play(trackList[index + 1])
    }

    override fun playPrevious() {
        val index = trackList.indexOf(currentTrack)
        if (index!=0)
            currentTrack = trackList[index-1]
        if (index - 1 >= 0) play(trackList[index - 1])
    }

    override fun getCurrentTrack(): Track? = currentTrack

    override fun observePlaybackState(): Flow<PlaybackState> = playbackState

    override fun observeProgress(): Flow<PlaybackProgress> = progressFlow
}