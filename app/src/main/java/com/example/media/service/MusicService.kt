package com.example.media.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.Toast
import androidx.media.MediaBrowserServiceCompat
import com.example.media.MainActivity
import com.example.media.R
import com.example.media.data.model.MusicFile
import com.example.media.utility.Constant
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.gson.Gson


class MusicService : MediaBrowserServiceCompat() {


    lateinit var exoPlayer: ExoPlayer


    private lateinit var playerNotificationManager: PlayerNotificationManager
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private lateinit var sharedPreferences: SharedPreferences


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

    private val mediaSessionCallback: MediaSessionCompat.Callback =
        object : MediaSessionCompat.Callback() {

            override fun onPlay() {
                super.onPlay()
                exoPlayer.play()
                mediaSession.isActive = true
                setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING)
            }


            override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
                super.onPlayFromUri(uri, extras)
                uri?.let {
                    extras?.let { bundle ->
                        bundle.getParcelable<MusicFile>(Constant.MUSIC_FILE)?.also { musicFile ->
                            setMediaMetaDataCompat(musicFile)
                        }
                    }
                    exoPlayer.setMediaSource(createMediaSource(it))
                    exoPlayer.prepare()
                    mediaSession.isActive = true
                    setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING)
                }
            }

            override fun onPause() {
                super.onPause()
                exoPlayer.pause()
                mediaSession.isActive = true
                setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED)
            }

            override fun onStop() {
                super.onStop()
                exoPlayer.stop()
                mediaSession.isActive = false
                setMediaPlaybackState(PlaybackStateCompat.STATE_STOPPED)
            }

            override fun onSeekTo(pos: Long) {
                super.onSeekTo(pos)
                exoPlayer.seekTo(pos)
                setMediaPlaybackState(PlaybackStateCompat.STATE_BUFFERING)
            }
        }


    override fun onCreate() {
        super.onCreate()
        initialize()

    }

    override fun onDestroy() {
        destroy()
        super.onDestroy()

    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot(getString(R.string.app_name), null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        result.sendResult(null)
    }


    private fun initialize() {
        exoPlayer = ExoPlayer.Builder(applicationContext).build()
        exoPlayer.playWhenReady = true
        playerNotificationManager = PlayerNotificationManager.Builder(
            applicationContext,
            Constant.NOTIFICATION_ID,
            Constant.CHANNEL_ID,
        ).setNotificationListener(
            playerNotificationListener
        ).setMediaDescriptionAdapter(mediaDescriptionAdapter)
            .setChannelNameResourceId(R.string.channel_name).build()
        playerNotificationManager.setPlayer(exoPlayer)
        mediaSession = MediaSessionCompat(applicationContext, Constant.MUSIC_MEDIA_SESSION_TAG)
        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSession.setCallback(mediaSessionCallback)
        mediaSessionConnector.setPlayer(exoPlayer)
        sessionToken = mediaSession.sessionToken
        sharedPreferences = getSharedPreferences(Constant.MEDIA_DB, Context.MODE_PRIVATE)
    }

    private fun setMediaMetaDataCompat(musicFile: MusicFile) {
        val gson = Gson()
        val edit = sharedPreferences.edit()
        val data = gson.toJson(musicFile)
        edit.putString(Constant.CURRENT_MUSIC_FILE, data)
        edit.apply()
    }

    private fun setMediaPlaybackState(state: Int) {
        val playBackStateBuilder = PlaybackStateCompat.Builder()
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            playBackStateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PAUSE)
        } else {
            playBackStateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PLAY)
        }
        playBackStateBuilder.setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0f)
        mediaSession.setPlaybackState(playBackStateBuilder.build())
    }

    private fun destroy() {
        playerNotificationManager.setPlayer(null)
        exoPlayer.release()
        Toast.makeText(applicationContext, "Destroyed", Toast.LENGTH_SHORT).show()
        mediaSession.isActive = false
        mediaSession.release()
    }


    private fun createMediaSource(fileUri: Uri): MediaSource {
        val dataSource = DefaultDataSource.Factory(applicationContext)
        return ProgressiveMediaSource.Factory(dataSource)
            .createMediaSource(MediaItem.fromUri(fileUri))
    }


}