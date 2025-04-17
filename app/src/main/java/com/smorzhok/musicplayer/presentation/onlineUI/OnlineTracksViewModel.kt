package com.smorzhok.musicplayer.presentation.onlineUI

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smorzhok.musicplayer.domain.model.Track
import com.smorzhok.musicplayer.domain.repository.TrackRepository
import com.smorzhok.musicplayer.domain.usecase.track.GetChartTracksUseCase
import com.smorzhok.musicplayer.domain.usecase.track.SearchTracksApiUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class OnlineTracksViewModel(
    private val repository: TrackRepository
) : ViewModel() {

    private val getChartTracksUseCase = GetChartTracksUseCase(repository)
    private val searchTracksApiUseCase = SearchTracksApiUseCase(repository)

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage

    private var isLoading = false
    private var isLastPage = false

    fun loadDefaultTracks() {
        if (isLoading || isLastPage) return
        isLoading = true

        viewModelScope.launch {
            try {
                val result = getChartTracksUseCase.invoke()
                _tracks.value += result

                if (result.size < 8) {
                    isLastPage = true
                }
            } catch (e: IOException) {
                _errorMessage.emit("Нет подключения к интернету")
            } catch (e: Exception) {
                _errorMessage.emit("Произошла ошибка")
            } finally {
                isLoading = false
            }
        }
    }

    fun searchTracks(query: String) {
        viewModelScope.launch {
            try {
                if (query.isBlank()) {
                    _tracks.value = emptyList()
                    isLastPage = false
                    loadDefaultTracks()
                } else {
                    isLoading = true
                    val result = searchTracksApiUseCase.invoke(query)
                    _tracks.value = result
                    isLastPage = true
                }
            } catch (e: IOException) {
                _errorMessage.emit("Нет подключения к интернету")
            } catch (e: Exception) {
                _errorMessage.emit("Произошла ошибка")
            } finally {
                isLoading = false
            }
        }
    }

    fun loadMoreTracks() {
        loadDefaultTracks()
    }

}