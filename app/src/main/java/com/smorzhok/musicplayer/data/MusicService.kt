package com.smorzhok.musicplayer.data

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.smorzhok.musicplayer.R
import com.smorzhok.musicplayer.domain.model.Track
import com.smorzhok.musicplayer.presentation.MainActivity

@UnstableApi
class MusicService : MediaSessionService() {

    private lateinit var mediaSession: MediaSession
    private lateinit var playerNotificationManager: PlayerNotificationManager

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val track = intent?.getParcelableExtra<Track>("EXTRA_TRACK")
        Log.d("MusicService", "Получен трек: ${track?.title}")

        track?.let {
            val player = getPlayer(this)
            val image = if (track.coverUrl == "")
                Uri.parse("android.resource://${packageName}/${R.drawable.photo_placeholder}")
            else Uri.parse(track.coverUrl)

            val mediaItem = MediaItem.Builder()
                .setUri(it.previewUrl)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(it.title)
                        .setArtist(it.artist)
                        .setAlbumTitle(it.album)
                        .setArtworkUri(image)
                        .build()
                )
                .build()

            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        }

        return START_STICKY
    }


    companion object {
        private var playerInstance: ExoPlayer? = null

        fun getPlayer(context: Context): ExoPlayer {
            return playerInstance ?: ExoPlayer.Builder(context.applicationContext).build().also {
                playerInstance = it
            }
        }
    }


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate() {
        super.onCreate()

        val notification = NotificationCompat.Builder(this, "player_channel")
            .setSmallIcon(R.drawable.play)
            .setContentTitle(getString(R.string.loading_music))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)

        val player = getPlayer(this)
        mediaSession = MediaSession.Builder(this, player).build()

        playerNotificationManager = PlayerNotificationManager.Builder(
            this,
            1,
            "player_channel"
        )
            .setMediaDescriptionAdapter(object : PlayerNotificationManager.MediaDescriptionAdapter {
                override fun getCurrentContentTitle(player: Player) =
                    player.currentMediaItem?.mediaMetadata?.title ?: "Unknown Title"

                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    val intent = Intent(this@MusicService, MainActivity::class.java)
                    return PendingIntent.getActivity(
                        this@MusicService, 0, intent, PendingIntent.FLAG_IMMUTABLE
                    )
                }

                override fun getCurrentContentText(player: Player): CharSequence? =
                    player.currentMediaItem?.mediaMetadata?.artist

                override fun getCurrentLargeIcon(
                    player: Player,
                    callback: PlayerNotificationManager.BitmapCallback
                ): Bitmap? = null
            })
            .setNotificationListener(object : PlayerNotificationManager.NotificationListener {
                override fun onNotificationPosted(id: Int, notification: Notification, ongoing: Boolean) {
                    Log.d("MusicService", "Notification posted: $id")
                }

                override fun onNotificationCancelled(id: Int, dismissedByUser: Boolean) {
                    stopForeground(true)
                    stopSelf()
                }
            })
            .build()

        playerNotificationManager.setMediaSessionToken(mediaSession.platformToken)
        playerNotificationManager.setPlayer(player)

    }
/*
    private fun createCustomNotification(player: Player): Notification {
        val remoteViews = RemoteViews(packageName, R.layout.notification_player)

        remoteViews.setTextViewText(
            R.id.title,
            player.currentMediaItem?.mediaMetadata?.title ?: "Unknown"
        )
        remoteViews.setTextViewText(
            R.id.artist,
            player.currentMediaItem?.mediaMetadata?.artist ?: "Unknown Artist"
        )

        val playPauseIntent = PendingIntent.getBroadcast(
            this, 0, Intent("ACTION_PLAY_PAUSE"), PendingIntent.FLAG_IMMUTABLE
        )
        val prevIntent = PendingIntent.getBroadcast(
            this, 1, Intent("ACTION_PREV"), PendingIntent.FLAG_IMMUTABLE
        )
        val nextIntent = PendingIntent.getBroadcast(
            this, 2, Intent("ACTION_NEXT"), PendingIntent.FLAG_IMMUTABLE
        )


        remoteViews.setOnClickPendingIntent(R.id.btn_play_pause, playPauseIntent)
        remoteViews.setOnClickPendingIntent(R.id.btn_prev, prevIntent)
        remoteViews.setOnClickPendingIntent(R.id.btn_next, nextIntent)

        Log.d("Doing", "Мы сюда вообще попадаем?")

        return NotificationCompat.Builder(this, "player_channel")
            .setSmallIcon(R.drawable.play)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(remoteViews)
            .setCustomBigContentView(remoteViews)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }*/

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