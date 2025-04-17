package com.smorzhok.musicplayer.presentation.playerUI

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smorzhok.musicplayer.domain.repository.PlayerRepository

@Suppress("UNCHECKED_CAST")
class PlayerViewModelFactory(
    private val playerRepository: PlayerRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlayerViewModel(playerRepository) as T
    }
}