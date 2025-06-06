package com.smorzhok.musicplayer.presentation.downloadedUI

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smorzhok.musicplayer.domain.model.Track
import com.smorzhok.musicplayer.domain.repository.TrackRepository
import com.smorzhok.musicplayer.domain.usecase.track.GetDownloadedTracksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DownloadedTracksViewModel(
    trackRepository: TrackRepository
) : ViewModel() {

    private val getDownloadedTracksUseCase = GetDownloadedTracksUseCase(trackRepository)
    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    private var allTracks: List<Track> = emptyList()

     fun loadDownloadedTracks() {
        viewModelScope.launch {
            allTracks = getDownloadedTracksUseCase.invoke()
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
