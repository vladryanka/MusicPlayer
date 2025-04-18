package com.smorzhok.musicplayer.data.repository

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.smorzhok.musicplayer.data.MusicService
import com.smorzhok.musicplayer.domain.model.PlaybackProgress
import com.smorzhok.musicplayer.domain.model.PlaybackState
import com.smorzhok.musicplayer.domain.model.Track
import com.smorzhok.musicplayer.domain.repository.PlayerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("DEPRECATION")
@UnstableApi
class PlayerRepositoryImpl(private val context: Context) : PlayerRepository {

    private val exoPlayer = MusicService.getPlayer(context)

    private var currentTrack: Track? = null

    private val _playbackError = MutableSharedFlow<String>(replay = 1)

    private val playbackState = MutableStateFlow(PlaybackState.STOPPED)
    private val _playbackState = MutableStateFlow(PlaybackState.STOPPED)
    private val playbackProgress = MutableStateFlow(PlaybackProgress(0, 0))
    private val _playbackProgress = MutableStateFlow(PlaybackProgress(0, 0))

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                _playbackError.tryEmit("Ошибка воспроизведения: ${error.message}")
                _playbackState.value = PlaybackState.STOPPED
            }
        })

        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                delay(1000)
                withContext(Dispatchers.Main) {
                    if (exoPlayer.isPlaying) {
                        val currentPosition = (exoPlayer.currentPosition / 1000).toInt()
                        val duration = (exoPlayer.duration / 1000).toInt()
                        _playbackProgress.value = PlaybackProgress(currentPosition, duration)
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

    override fun getPlaybackError():  SharedFlow<String> = _playbackError


    override fun getTrackList(): List<Track> = trackList

    @OptIn(UnstableApi::class)
    override fun play(track: Track) {
        currentTrack = track

        if (!isNetworkAvailable()) {
            _playbackError.tryEmit("Нет подключения к интернету")
            return
        }
        val intent = Intent(context, MusicService::class.java).apply {
            putExtra("EXTRA_TRACK", currentTrack)
        }
        ContextCompat.startForegroundService(context, intent)

        try {
            val mediaItem = MediaItem.fromUri(track.previewUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()

            playbackState.value = PlaybackState.PLAYING
            playbackProgress.value = PlaybackProgress(0, track.duration)
        } catch (e: Exception) {
            playbackState.value = PlaybackState.STOPPED
        }

    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    override fun pause() {
        exoPlayer.pause()
        playbackState.value = PlaybackState.PAUSED
    }

    override fun resume() {
        exoPlayer.play()
        playbackState.value = PlaybackState.PLAYING
    }

    override fun seekTo(position: Int) {
        currentTrack?.let {
            playbackProgress.value = PlaybackProgress(position, it.duration)
            exoPlayer.seekTo(position * 1000L)
        }
    }

    override fun playNext() {
        val index = trackList.indexOf(currentTrack)
        if (index != trackList.size - 1)
            currentTrack = trackList[index + 1]
        if (index + 1 < trackList.size) play(trackList[index + 1])
    }

    override fun playPrevious() {
        val index = trackList.indexOf(currentTrack)
        if (index != 0)
            currentTrack = trackList[index - 1]
        if (index - 1 >= 0) play(trackList[index - 1])
    }

    override fun getCurrentTrack(): Track? = currentTrack

    override fun observePlaybackState(): Flow<PlaybackState> = playbackState

    override fun observeProgress(): Flow<PlaybackProgress> = _playbackProgress
}