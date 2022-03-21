package com.example.media.service

import android.annotation.SuppressLint
import android.app.Application
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.IBinder
import android.widget.Toast
import com.example.media.MainActivity
import com.example.media.R
import com.example.media.data.model.MusicFile
import com.example.media.utility.Constant
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.util.NotificationUtil

class MusicService : Service() {


    lateinit var exoPlayer: ExoPlayer


    private lateinit var playerNotificationManager: PlayerNotificationManager

    private val binder = LocalBinder()


    private val playerNotificationListener =
        object : PlayerNotificationManager.NotificationListener {
            override fun onNotificationPosted(
                notificationId: Int,
                notification: Notification,
                ongoing: Boolean
            ) {
                startForeground(notificationId, notification)
            }

            override fun onNotificationCancelled(
                notificationId: Int,
                dismissedByUser: Boolean
            ) {
                stopSelf()
            }
        }

    private val mediaDescriptionAdapter =
        object : PlayerNotificationManager.MediaDescriptionAdapter {
            override fun getCurrentContentTitle(player: Player): CharSequence {
                return player.mediaMetadata.displayTitle ?: player.mediaMetadata.title
                ?: "Music ..."
            }

            @SuppressLint("UnspecifiedImmutableFlag")
            override fun createCurrentContentIntent(player: Player): PendingIntent? {
                val intent = Intent(applicationContext, MainActivity::class.java)
                return PendingIntent.getActivity(
                    applicationContext,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

            override fun getCurrentContentText(player: Player): CharSequence? {
                return player.mediaMetadata.albumArtist
            }

            override fun getCurrentLargeIcon(
                player: Player,
                callback: PlayerNotificationManager.BitmapCallback
            ): Bitmap? {
                return player.mediaMetadata.artworkData?.let {
                    BitmapFactory.decodeByteArray(
                        it, 0,
                        it.size
                    )
                }
            }

        }

    override fun onBind(intent: Intent): IBinder {
        onStart()
        return binder
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        onStart()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        onStart()
        return START_STICKY
    }

    override fun onDestroy() {
        playerNotificationManager.setPlayer(null)
        exoPlayer.release()
        Toast.makeText(applicationContext, "Destoryed", Toast.LENGTH_SHORT).show()
        super.onDestroy()

    }

    private fun onStart() {
        Toast.makeText(applicationContext, "Starting 1", Toast.LENGTH_SHORT).show()

        exoPlayer = ExoPlayer.Builder(applicationContext).build()
        playerNotificationManager = PlayerNotificationManager.Builder(
            applicationContext,
            Constant.NOTIFICATION_ID,
            Constant.CHANNEL_ID,
        ).setNotificationListener(
            playerNotificationListener
        ).setMediaDescriptionAdapter(mediaDescriptionAdapter)
            .setChannelNameResourceId(R.string.channel_name).build()
        playerNotificationManager.setPlayer(exoPlayer)
        Toast.makeText(applicationContext, "Starting 2", Toast.LENGTH_SHORT).show()
    }

    fun loadMusicFile(musicFile: MusicFile) {
        if (exoPlayer.isPlaying) {
            stop()
        }
        exoPlayer.setMediaSource(createMediaSource(musicFile))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true

    }

    fun play() {
        exoPlayer.play()
    }

    fun stop() {
        exoPlayer.stop()
    }

    fun pause() {
        exoPlayer.pause()
    }


    private fun createMediaSource(musicFile: MusicFile): MediaSource {
        val dataSource = DefaultDataSource.Factory(applicationContext)
        return ProgressiveMediaSource.Factory(dataSource)
            .createMediaSource(MediaItem.fromUri(musicFile.fileUri))
    }


    inner class LocalBinder : Binder() {
        fun getService() = this@MusicService
    }


}