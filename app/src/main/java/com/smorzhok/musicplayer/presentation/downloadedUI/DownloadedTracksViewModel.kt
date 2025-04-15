package com.smorzhok.musicplayer.presentation.downloadedUI

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smorzhok.musicplayer.domain.model.Track
import com.smorzhok.musicplayer.domain.repository.TrackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DownloadedTracksViewModel(
    private val trackRepository: TrackRepository
) : ViewModel() {

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    private var allTracks: List<Track> = emptyList()

    init {
        loadDownloadedTracks()
    }

    private fun loadDownloadedTracks() {
        viewModelScope.launch {
            allTracks = trackRepository.getDownloadedTracks()
            _tracks.value = allTracks
        }
    }

    fun searchTracks(query: String) {
        viewModelScope.launch {
            _tracks.value = if (query.isBlank()) {
                allTracks
            } else {
                allTracks.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.artist.contains(query, ignoreCase = true)
                }
            }
        }
    }
}
