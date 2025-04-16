package com.smorzhok.musicplayer.presentation.onlineUI

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smorzhok.musicplayer.domain.repository.TrackRepository

@Suppress("UNCHECKED_CAST")
class OnlineTracksViewModelFactory(
    private val repository: TrackRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OnlineTracksViewModel(repository) as T
    }
}