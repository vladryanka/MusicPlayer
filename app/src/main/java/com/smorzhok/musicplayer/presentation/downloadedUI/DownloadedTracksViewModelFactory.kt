package com.smorzhok.musicplayer.presentation.downloadedUI

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smorzhok.musicplayer.domain.repository.TrackRepository

@Suppress("UNCHECKED_CAST")
class DownloadedTracksViewModelFactory(
    private val trackRepository: TrackRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DownloadedTracksViewModel::class.java)) {
            return DownloadedTracksViewModel(trackRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}