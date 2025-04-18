package com.smorzhok.musicplayer.data.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.smorzhok.musicplayer.data.service.MusicService

class NotificationActionReceiver : BroadcastReceiver() {
    @OptIn(UnstableApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "ACTION_PLAY_PAUSE" -> {
                val player = MusicService.getPlayer(context)
                if (player.isPlaying) {
                    player.pause()
                } else {
                    player.play()
                }
            }
            "ACTION_NEXT" -> {
                val serviceIntent = Intent(context, MusicService::class.java)
                serviceIntent.action = "ACTION_NEXT"
                context.startService(serviceIntent)
            }
            "ACTION_PREV" -> {
                val serviceIntent = Intent(context, MusicService::class.java)
                serviceIntent.action = "ACTION_PREV"
                context.startService(serviceIntent)
            }

        }
    }

}
