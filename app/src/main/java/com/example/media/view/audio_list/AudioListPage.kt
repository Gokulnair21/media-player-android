package com.example.media.view.audio_list

import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.media.R
import com.example.media.base.BaseFragment
import com.example.media.data.model.MusicFile
import com.example.media.databinding.AudioListPageFragmentBinding
import com.example.media.service.MusicService
import com.example.media.utility.Constant
import com.example.media.utility.Resource
import com.example.media.view.custom_views.AudioControllerView
import com.google.gson.Gson

class AudioListPage : BaseFragment<AudioListPageFragmentBinding>() {


    private val viewModel by viewModels<AudioListPageViewModel> {
        AudioListPageViewModel.Factory(
            requireContext().contentResolver, requireContext().getSharedPreferences(
                Constant.MEDIA_DB, Context.MODE_PRIVATE
            )
        )
    }

    private val audioAdapter: AudioAdapter by lazy {
        AudioAdapter(requireContext().contentResolver) {
            val mediaController = MediaControllerCompat.getMediaController(requireActivity())
            val extra = Bundle()
            extra.putParcelable(Constant.MUSIC_FILE, it)
            mediaController.transportControls.playFromUri(Uri.parse(it.fileUri), extra)
        }
    }
    private val sharedPreferences: SharedPreferences by lazy {
        requireContext().getSharedPreferences(Constant.MEDIA_DB, Context.MODE_PRIVATE)
    }

    private lateinit var mediaBrowser: MediaBrowserCompat

    private val mediaControllerCallback = object : MediaControllerCompat.Callback() {

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)
            //
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            state?.let {
                when (it.state) {
                    PlaybackStateCompat.STATE_PAUSED -> {
                        binding.audioController.showPlayButton = true
                    }
                    PlaybackStateCompat.STATE_PLAYING -> {
                        if (binding.audioController.visibility == View.GONE) {
                            binding.audioController.visibility = View.VISIBLE
                        }
                        binding.audioController.showPauseButton = true
                    }

                }
            }

        }

        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
            mediaBrowser.disconnect()
        }
    }


    private val mediaConnectionCallBack = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            super.onConnected()
            buildUI()
        }

        override fun onConnectionFailed() {
            super.onConnectionFailed()
        }

    }

    private val audioControllerListener: AudioControllerView.OnClickListener =
        object : AudioControllerView.OnClickListener {

            override fun play() {
                val mediaController = MediaControllerCompat.getMediaController(requireActivity())
                mediaController.transportControls.play()
            }

            override fun pause() {
                val mediaController = MediaControllerCompat.getMediaController(requireActivity())
                mediaController.transportControls.pause()
            }

            override fun next() {
                val mediaController = MediaControllerCompat.getMediaController(requireActivity())
                mediaController.transportControls.skipToNext()
            }

            override fun previous() {
                val mediaController = MediaControllerCompat.getMediaController(requireActivity())
                mediaController.transportControls.skipToPrevious()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaBrowser = MediaBrowserCompat(
            requireContext(),
            ComponentName(requireContext(), MusicService::class.java),
            mediaConnectionCallBack, null

        )

    }

    override fun onStart() {
        super.onStart()
        if (!mediaBrowser.isConnected) {
            mediaBrowser.connect()
        }
    }


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = AudioListPageFragmentBinding.inflate(inflater, container, false)


    override fun setUpViews() {
        binding.audioList.adapter = audioAdapter
        binding.audioController.listener = audioControllerListener
        binding.audioController.visibility = View.GONE

    }

    override fun observeData() {
        viewModel.audios.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    state.data?.let {
                        audioAdapter.updateDataList(it)
                    }
                }
                is Resource.Error -> {
                    showToast(state.error.toString())
                }

            }
        }
        viewModel.currentMusicFile.observe(viewLifecycleOwner) {
            setUpUIForAudioController(it)
        }
    }


    override fun onDestroy() {
        MediaControllerCompat.getMediaController(requireActivity())
            ?.unregisterCallback(mediaControllerCallback)
        mediaBrowser.disconnect()
        super.onDestroy()
    }


    private fun buildUI() {
        val mediaController = MediaControllerCompat(requireContext(), mediaBrowser.sessionToken)
        MediaControllerCompat.setMediaController(requireActivity(), mediaController)
        mediaController.registerCallback(mediaControllerCallback)

    }

    private fun setUpUIForAudioController(musicFile: MusicFile?) {

        if (musicFile != null) {
            binding.audioController.title = musicFile.name
            binding.audioController.artistName = musicFile.artistName ?: "Unknown"
            binding.audioController.image=musicFile.thumbnail
            binding.audioController.visibility = View.VISIBLE

        } else {
            binding.audioController.visibility = View.GONE
        }

    }


}