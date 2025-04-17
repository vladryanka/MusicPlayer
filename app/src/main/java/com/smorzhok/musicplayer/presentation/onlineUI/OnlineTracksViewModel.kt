package com.smorzhok.musicplayer.presentation.onlineUI

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smorzhok.musicplayer.domain.model.Track
import com.smorzhok.musicplayer.domain.repository.TrackRepository
import com.smorzhok.musicplayer.domain.usecase.track.GetChartTracksUseCase
import com.smorzhok.musicplayer.domain.usecase.track.SearchTracksApiUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OnlineTracksViewModel(
    private val repository: TrackRepository
) : ViewModel() {

    private val getChartTracksUseCase = GetChartTracksUseCase(repository)
    private val searchTracksApiUseCase = SearchTracksApiUseCase(repository)

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    private var isLoading = false
    private var isLastPage = false

    fun loadDefaultTracks() {
        if (isLoading || isLastPage) return
        isLoading = true

        viewModelScope.launch {
            val result = getChartTracksUseCase.invoke()
            _tracks.value = _tracks.value + result
            isLoading = false

            if (result.size < 8) {
                isLastPage = true
            }
        }
    }

    fun loadMoreTracks() {
        loadDefaultTracks()
    }

    fun searchTracks(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _tracks.value = emptyList()
                isLastPage = false
                loadDefaultTracks()
            } else {
                isLoading = true
                val result = searchTracksApiUseCase.invoke(query)
                _tracks.value = result
                isLoading = false
                isLastPage = true
            }

            Log.d("Doing", "Треки: " + _tracks.toString())
        }
    }
}