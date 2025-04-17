package com.smorzhok.musicplayer.presentation.playerUI

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smorzhok.musicplayer.domain.model.Track
import com.smorzhok.musicplayer.domain.repository.PlayerRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerRepository: PlayerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var updateJob: Job? = null

    init {
        viewModelScope.launch {
            playerRepository.observePlaybackState().collect { state ->
                _uiState.update { it.copy(playbackState = state) }
            }
        }

        viewModelScope.launch {
            playerRepository.observeProgress().collect { progress ->
                _uiState.update { it.copy(progress = progress) }
            }
        }

        _uiState.update { it.copy(track = playerRepository.getCurrentTrack()) }
    }

    fun play(track: Track) {
        playerRepository.play(track)
        _uiState.update { it.copy(track = track) }
    }

    fun pause() = playerRepository.pause()
    fun resume() = playerRepository.resume()
    fun seekTo(position: Int) = playerRepository.seekTo(position)
    fun next() = playerRepository.playNext()
    fun previous() = playerRepository.playPrevious()
}


