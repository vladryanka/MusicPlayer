package com.smorzhok.musicplayer.data.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.smorzhok.musicplayer.R
import com.smorzhok.musicplayer.data.broadcast.NotificationActionReceiver
import com.smorzhok.musicplayer.data.repository.PlayerRepositoryImpl
import com.smorzhok.musicplayer.data.service.PlayerRepositoryHolder.playerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@UnstableApi
class MusicService : MediaSessionService() {

    private lateinit var mediaSession: MediaSession

    private var wasNotifiedOnce = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {
            "ACTION_NEXT" -> {
                playNextFromNotification()
                return START_STICKY
            }
            "ACTION_PREV" -> {
                playPrevFromNotification()
                return START_STICKY
            }
            "ACTION_PLAY_PAUSE" -> {
                togglePlayPause()
                showNotification()
                return START_STICKY
            }
            "ACTION_UPDATE_NOTIFICATION" -> {
                showNotification()
                return START_STICKY
            }
        }
        val currentTrack = playerRepository?.getCurrentTrack()
        Log.d("MusicService", "Получен трек: ${currentTrack?.title}")

        currentTrack?.let {
            val player = getPlayer(this)
            val mediaItem = MediaItem.Builder()
                .setUri(it.previewUrl)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(it.title)
                        .setArtist(it.artist)
                        .setAlbumTitle(it.album.title)
                        .setArtworkUri(Uri.parse(it.coverUrl))
                        .build()
                )
                .build()

            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
            showNotification()
        }

        return START_STICKY
    }
    private fun togglePlayPause() {
        val player = getPlayer(this)
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
        CoroutineScope(Dispatchers.Main).launch {
            kotlinx.coroutines.delay(200)
            showNotification()
        }
    }

    private fun playNextFromNotification() {
        if (!wasNotifiedOnce) {
            CoroutineScope(Dispatchers.IO).launch {
                playerRepository?.changeListFromNotificationPlayer()
            }
            wasNotifiedOnce = true
        } else {
            val repo = playerRepository
            repo?.playNext()
        }
    }

    private fun playPrevFromNotification() {
        val repo = playerRepository
        repo?.playPrevious()
    }


    companion object {
        private var playerInstance: ExoPlayer? = null

        private const val CHANNEL_ID = "music_channel"

        fun getPlayer(context: Context): ExoPlayer {
            return playerInstance ?: ExoPlayer.Builder(context.applicationContext).build().also {
                playerInstance = it
            }
        }
    }


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        val player = getPlayer(this)
        mediaSession = MediaSession.Builder(this, player).build()
    }

    @SuppressLint("MissingPermission")
    private fun createCustomNotification(player: Player) {
        val remoteViews = createRemoteViews(player)

        val builder = NotificationCompat.Builder(this@MusicService, CHANNEL_ID)
            .setSmallIcon(R.drawable.play)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(remoteViews)
            .setCustomBigContentView(remoteViews)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)

        val notification = builder.build()

        if (!wasNotifiedOnce) {
            startForeground(1, notification)
            wasNotifiedOnce = true
        } else {
            NotificationManagerCompat.from(this).notify(1, notification)
        }

        val coverUrl = player.currentMediaItem?.mediaMetadata?.artworkUri?.toString()

        if (!coverUrl.isNullOrEmpty()) {
            Glide.with(this)
                .asBitmap()
                .load(coverUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val updatedRemoteViews = createRemoteViews(player, resource)

                        val updatedNotification = builder
                            .setCustomContentView(updatedRemoteViews)
                            .setCustomBigContentView(updatedRemoteViews)
                            .build()

                        NotificationManagerCompat.from(this@MusicService)
                            .notify(1, updatedNotification)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        val fallbackBitmap = BitmapFactory.decodeResource(
                            resources,
                            R.drawable.photo_placeholder
                        )
                        val fallbackRemoteViews = createRemoteViews(player, fallbackBitmap)
                        val fallbackNotification = builder
                            .setCustomContentView(fallbackRemoteViews)
                            .setCustomBigContentView(fallbackRemoteViews)
                            .build()

                        NotificationManagerCompat.from(this@MusicService)
                            .notify(1, fallbackNotification)
                    }
                })
        }
    }

    private fun createRemoteViews(player: Player, albumBitmap: Bitmap? = null): RemoteViews {
        val remoteViews = RemoteViews(packageName, R.layout.notification_player)

        remoteViews.setImageViewResource(
            R.id.btn_play_pause,
            if (player.isPlaying) R.drawable.pause else R.drawable.play
        )

        remoteViews.setTextViewText(
            R.id.title,
            player.currentMediaItem?.mediaMetadata?.title ?: ""
        )
        remoteViews.setTextViewText(
            R.id.artist,
            player.currentMediaItem?.mediaMetadata?.artist ?: ""
        )

        remoteViews.setOnClickPendingIntent(R.id.btn_play_pause, createPendingIntent("ACTION_PLAY_PAUSE"))
        remoteViews.setOnClickPendingIntent(R.id.btn_prev, createPendingIntent("ACTION_PREV"))
        remoteViews.setOnClickPendingIntent(R.id.btn_next, createPendingIntent("ACTION_NEXT"))

        if (albumBitmap != null) {
            remoteViews.setImageViewBitmap(R.id.track_icon, albumBitmap)
        } else {
            remoteViews.setImageViewResource(R.id.track_icon, R.drawable.photo_placeholder)
        }

        return remoteViews
    }


    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, NotificationActionReceiver::class.java).apply {
            this.action = action
        }
        return PendingIntent.getBroadcast(
            this,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }



    private fun createNotificationChannel() {

        Log.d("Doing", "Попали в createNotificationChannel")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Player",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }


    private fun showNotification() {
        if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            val player = getPlayer(this)
            createCustomNotification(player)
        } else {
            Log.w("MusicService", "Notifications are disabled, skipping showNotification()")
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession.release()
        playerInstance?.release()
        playerInstance = null
        Log.d("Doing", "onDestroy")
        super.onDestroy()
    }
}

@UnstableApi
object PlayerRepositoryHolder {
    @SuppressLint("StaticFieldLeak")
    var playerRepository: PlayerRepositoryImpl? = null
}