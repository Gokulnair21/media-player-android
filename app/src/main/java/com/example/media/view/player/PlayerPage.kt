package com.example.media.view.player

import android.content.pm.ActivityInfo
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.media.R
import com.example.media.data.model.MediaFile
import com.example.media.databinding.ActivityPlayerPageBinding
import com.example.media.utility.Constant
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.TracksInfo
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
        binding.player.findViewById<ImageButton>(R.id.orientation).setOnClickListener {
            changeOrientation()
        }
        binding.player.findViewById<ImageButton>(R.id.lockScreen).setOnClickListener {
            lockScreen()
        }
        binding.lockButton.setOnClickListener {
            lockScreen()
        }
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

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        if (isPlaying) {
            hideStatusBar()
        } else {
            showStatusBar()
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        intent.getParcelableArrayListExtra<MediaFile>(Constant.MEDIA_FILE)?.let {
            binding.player.findViewById<TextView>(R.id.videoTitle).text =it[player.currentMediaItemIndex].name

        }
    }


    private fun hideStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun showStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.show(WindowInsets.Type.statusBars())
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        }
    }

    private fun changeOrientation() {
        val orientation = requestedOrientation
        requestedOrientation = if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    private fun lockScreen() {
        binding.player.useController = !binding.player.useController
        binding.lockButton.isVisible = !binding.player.useController
        if (binding.player.useController) {
            binding.player.showController()
        }
    }


    private fun initializePlayer() {
        player = ExoPlayer.Builder(this).build()
        binding.player.player = player
        intent.getParcelableArrayListExtra<MediaFile>(Constant.MEDIA_FILE)?.let {
            player.setMediaSources(getMediaSourceFromURI(it.toList()))
            if (currentPosition == 0.toLong()) {
                player.seekTo(
                    intent.getIntExtra(Constant.MEDIA_FILE_TO_BE_PLAYED, 0),
                    currentPosition
                )
            } else {
                player.seekTo(currentPosition)
            }
            binding.player.findViewById<TextView>(R.id.videoTitle).text =it[player.currentMediaItemIndex].name
            player.prepare()
            player.addListener(this)
            player.playWhenReady = true
        }

    }

    private fun releasePlayer() {
        currentPosition = player.currentPosition
        player.release()
    }

    private fun getMediaSourceFromURI(list: List<MediaFile>): ArrayList<MediaSource> {
        val dataSource = DefaultDataSource.Factory(this)
        val mediaSourceList = ArrayList<MediaSource>()
        list.forEach {
            val value = ProgressiveMediaSource.Factory(dataSource)
                .createMediaSource(MediaItem.fromUri(it.fileUri))
            mediaSourceList.add(value)
        }
        return mediaSourceList
    }


}