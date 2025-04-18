package com.smorzhok.musicplayer.presentation.playerUI

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smorzhok.musicplayer.domain.model.Track
import com.smorzhok.musicplayer.domain.repository.PlayerRepository
import com.smorzhok.musicplayer.domain.usecase.player.GetCurrentTrackUseCase
import com.smorzhok.musicplayer.domain.usecase.player.GetPlaybackError
import com.smorzhok.musicplayer.domain.usecase.player.GetTrackListUseCase
import com.smorzhok.musicplayer.domain.usecase.player.ObservePlaybackStateUseCase
import com.smorzhok.musicplayer.domain.usecase.player.ObserveProgressUseCase
import com.smorzhok.musicplayer.domain.usecase.player.PauseTrackUseCase
import com.smorzhok.musicplayer.domain.usecase.player.PlayNextTrackUseCase
import com.smorzhok.musicplayer.domain.usecase.player.PlayPreviousTrackUseCase
import com.smorzhok.musicplayer.domain.usecase.player.PlayTrackUseCase
import com.smorzhok.musicplayer.domain.usecase.player.ResumeTrackUseCase
import com.smorzhok.musicplayer.domain.usecase.player.SeekToUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerRepository: PlayerRepository
) : ViewModel() {

    private val getTrackListUseCase = GetTrackListUseCase(playerRepository)
    private val playTrackUseCase = PlayTrackUseCase(playerRepository)
    private val observePlaybackStateUseCase = ObservePlaybackStateUseCase(playerRepository)
    private val observeProgressUseCase = ObserveProgressUseCase(playerRepository)
    private val getCurrentTrackUseCase = GetCurrentTrackUseCase(playerRepository)
    private val resumeTrackUseCase = ResumeTrackUseCase(playerRepository)
    private val playPreviousTrackUseCase = PlayPreviousTrackUseCase(playerRepository)
    private val playNextTrackUseCase = PlayNextTrackUseCase(playerRepository)
    private val getPlaybackError = GetPlaybackError(playerRepository)
    private val seekToUseCase = SeekToUseCase(playerRepository)
    private val pauseTrackUseCase = PauseTrackUseCase(playerRepository)

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String>("")
    val errorMessage: SharedFlow<String> = _errorMessage

    fun initWithTrackIndex(index: Int) {
        Log.d("Doing", index.toString())
        val track = getTrackListUseCase.invoke()[index]
        _uiState.value = PlayerUiState(track = track)
        Log.d("Doing", track.title)
        playTrackUseCase.invoke(track)
    }

    init {
        viewModelScope.launch {
            observePlaybackStateUseCase.invoke().collect { state ->
                _uiState.update { it.copy(playbackState = state) }
            }
        }
        viewModelScope.launch {
            getPlaybackError.invoke().collect { errorMessage ->
                _errorMessage.emit(errorMessage)
            }
        }

        viewModelScope.launch {
            observeProgressUseCase.invoke().collect { progress ->
                _uiState.update { it.copy(progress = progress) }
            }
        }

        _uiState.update { it.copy(track = getCurrentTrackUseCase.invoke()) }
    }

    fun retry() {
        uiState.value.track?.let {
            play(it)
        }
    }

    fun play(track: Track) {
        viewModelScope.launch {
                try {
                    playTrackUseCase.invoke(track)
                    _uiState.update { it.copy(track = track) }
                    _errorMessage.emit("")
                } catch (e: Exception) {
                    _errorMessage.emit("${e.message}")
                }
        }
    }

    fun pause() = pauseTrackUseCase.invoke()
    fun resume() = resumeTrackUseCase.invoke()
    fun seekTo(position: Int) = seekToUseCase.invoke(position)
    fun next() {
        playNextTrackUseCase.invoke()
        _uiState.update {
            it.copy(getCurrentTrackUseCase.invoke())
        }
    }


    fun previous() {
        playPreviousTrackUseCase.invoke()
        _uiState.update {
            it.copy(getCurrentTrackUseCase.invoke())
        }
    }

}


