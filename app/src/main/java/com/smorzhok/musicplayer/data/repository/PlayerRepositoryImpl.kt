package com.smorzhok.musicplayer.data.repository

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.smorzhok.musicplayer.data.service.MusicService
import com.smorzhok.musicplayer.data.service.PlayerRepositoryHolder
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
class PlayerRepositoryImpl(
    private val context: Context
) : PlayerRepository {

    private val exoPlayer = MusicService.getPlayer(context)

    private var currentTrack: Track? = null

    private val _playbackError = MutableSharedFlow<String>(replay = 1)
    private val _playbackState = MutableStateFlow(PlaybackState.STOPPED)
    private val _playbackProgress = MutableStateFlow(PlaybackProgress(0, 0))

    private val trackList = mutableListOf<Track>()

    init {
        PlayerRepositoryHolder.playerRepository = this
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                _playbackError.tryEmit("Ошибка воспроизведения: ${error.message}")
                _playbackState.value = PlaybackState.STOPPED
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playbackState.value = if (isPlaying) PlaybackState.PLAYING else PlaybackState.PAUSED
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    playNext()
                }
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
                        val intent = Intent(context, MusicService::class.java).apply {
                            action = "ACTION_UPDATE_NOTIFICATION"
                        }
                        context.startService(intent)
                    }
                }
            }
        }
    }

    override fun setTrackList(tracks: List<Track>, selectedIndex: Int) {
        trackList.clear()
        trackList.addAll(tracks)
        currentTrack = trackList.getOrNull(selectedIndex)
    }

    override fun getTrackList(): List<Track> = trackList

    override fun getCurrentTrack(): Track? = currentTrack

    override fun getPlaybackError(): SharedFlow<String> = _playbackError

    override fun observePlaybackState(): Flow<PlaybackState> = _playbackState

    override fun observeProgress(): Flow<PlaybackProgress> = _playbackProgress

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
            _playbackProgress.value = PlaybackProgress(0, track.duration)
        } catch (e: Exception) {
            _playbackState.value = PlaybackState.STOPPED
        }
    }

    override fun pause() {
        exoPlayer.pause()
    }

    override fun resume() {
        exoPlayer.play()
    }

    override fun seekTo(position: Int) {
        currentTrack?.let {
            _playbackProgress.value = PlaybackProgress(position, it.duration)
            exoPlayer.seekTo(position * 1000L)
        }
    }

    override fun playNext() {
        val index = trackList.indexOf(currentTrack)
        val nextIndex = index + 1
        if (nextIndex in trackList.indices) {
            play(trackList[nextIndex])
        }
    }

    override fun playPrevious() {
        val index = trackList.indexOf(currentTrack)
        val prevIndex = index - 1
        if (prevIndex in trackList.indices) {
            play(trackList[prevIndex])
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }
}