package com.smorzhok.musicplayer.data

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.smorzhok.musicplayer.presentation.MainActivity

@UnstableApi
class MusicService : MediaSessionService() {

    private lateinit var mediaSession: MediaSession
    private lateinit var playerNotificationManager: PlayerNotificationManager

    companion object {
        private var playerInstance: ExoPlayer? = null

        fun getPlayer(context: Context): ExoPlayer {
            return playerInstance ?: ExoPlayer.Builder(context.applicationContext).build().also {
                playerInstance = it
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        val player = getPlayer(this)

        // создаём MediaSession из media3
        mediaSession = MediaSession.Builder(this, player).build()

        playerNotificationManager = PlayerNotificationManager.Builder(
            this,
            1,
            "player_channel"
        )
            .setMediaDescriptionAdapter(object : PlayerNotificationManager.MediaDescriptionAdapter {
                override fun getCurrentContentTitle(player: Player): CharSequence {
                    return player.currentMediaItem?.mediaMetadata?.title ?: "Unknown Title"
                }

                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    val intent = Intent(this@MusicService, MainActivity::class.java)
                    return PendingIntent.getActivity(this@MusicService, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                }

                override fun getCurrentContentText(player: Player): CharSequence? {
                    return player.currentMediaItem?.mediaMetadata?.artist
                }

                override fun getCurrentLargeIcon(
                    player: Player,
                    callback: PlayerNotificationManager.BitmapCallback
                ): Bitmap? = null
            })
            .setNotificationListener(object : PlayerNotificationManager.NotificationListener {
                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: Notification,
                    ongoing: Boolean
                ) {
                    startForeground(notificationId, notification)
                }

                override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
                    stopForeground(true)
                    stopSelf()
                }
            })
            .build()

        playerNotificationManager.setMediaSessionToken(mediaSession.platformToken)
        playerNotificationManager.setPlayer(player)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession.release()
        playerInstance?.release()
        playerInstance = null
        super.onDestroy()
    }
}