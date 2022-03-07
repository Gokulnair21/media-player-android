package com.example.media.view.player

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.media.data.model.MediaFile
import com.example.media.databinding.ActivityPlayerPageBinding
import com.example.media.utility.Constant
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource

class PlayerPage : AppCompatActivity(), Player.Listener {

    private val binding by lazy {
        ActivityPlayerPageBinding.inflate(layoutInflater)
    }


    private lateinit var player: ExoPlayer
    private var currentPosition: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }


    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }


    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        super.onPlayWhenReadyChanged(playWhenReady, reason)
    }


    private fun initializePlayer() {
        player = ExoPlayer.Builder(this).build()
        binding.player.player = player
        intent.getParcelableExtra<MediaFile>(Constant.MEDIA_FILE)?.let {
            player.setMediaSource(getMediaSourceFromURI(this, it.fileUri))
            player.seekTo(currentPosition)
            player.playWhenReady = true
            player.prepare()
        }

    }

    private fun releasePlayer() {
        currentPosition = player.currentPosition
        player.release()
    }

    private fun getMediaSourceFromURI(context: Context, uri: Uri): MediaSource {
        val dataSource = DefaultDataSource.Factory(context)
        return ProgressiveMediaSource.Factory(dataSource)
            .createMediaSource(MediaItem.fromUri(uri))
    }


    onP
}