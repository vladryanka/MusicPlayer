package com.smorzhok.musicplayer.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi

class NotificationActionReceiver : BroadcastReceiver() {
    @OptIn(UnstableApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        val player = MusicService.getPlayer(context)
        when (intent.action) {
            "ACTION_PLAY_PAUSE" -> {
                // Вставь логику для переключения между паузой и воспроизведением

                Log.d("MusicControlReceiver", "Play/Pause clicked")
                if (player.isPlaying) {
                    player.pause()
                } else {
                    player.play()
                }
            }
            "ACTION_NEXT" -> {
                // Логика для следующего трека
            }
            "ACTION_PREV" -> {
                // Логика для предыдущего трека
            }
        }
    }
}
