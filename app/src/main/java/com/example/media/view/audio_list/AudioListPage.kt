package com.example.media.view.audio_list

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.example.media.R
import com.example.media.base.BaseFragment
import com.example.media.databinding.AudioListPageFragmentBinding
import com.example.media.service.MusicService
import com.example.media.utility.Resource
import com.example.media.view.custom_views.AudioControllerView

class AudioListPage : BaseFragment<AudioListPageFragmentBinding>(),
    AudioControllerView.OnClickListener {

    private lateinit var musicService: MusicService
    private var musicServiceBound = false
    private val viewModel by viewModels<AudioListPageViewModel>()
    private val audioControlViewModel by activityViewModels<AudioControlViewModel>()

    private val audioAdapter = AudioAdapter {
        if (musicServiceBound) {
            musicService.loadMusicFile(it)
        } else {
            requireActivity().bindService(
                Intent(
                    requireContext(),
                    MusicService::class.java
                ),
                serviceConnection,
                Context.BIND_AUTO_CREATE
            )
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.LocalBinder
            musicService = binder.getService()
            musicServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicServiceBound = false
        }
    }


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = AudioListPageFragmentBinding.inflate(inflater, container, false)


    override fun setUpViews() {
        binding.audioList.adapter = audioAdapter
        binding.audioController.listener = this
        binding.audioController.title = "Gokyul song"
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

        audioControlViewModel.musicFileLiveData.observe(viewLifecycleOwner) {
            binding.audioController.title = it.name
            it.thumbnail?.let { bitmap ->
                val paletteBuilder = Palette.Builder(bitmap)
                val palette = paletteBuilder.generate()
                val swatch = palette.dominantSwatch
                swatch?.let { swatchP ->
                    binding.audioController.setBackgroundColor(swatchP.rgb)
                } ?: binding.audioController.setBackgroundColor(resources.getColor(R.color.blue))

            } ?: binding.audioController.setBackgroundColor(resources.getColor(R.color.blue))
            Glide.with(requireContext()).load(it.thumbnail)
                .error(R.drawable.ic_baseline_music_note_24)
                .into(binding.audioController.image)

        }
    }


    override fun play() {
        if (musicServiceBound) {
            musicService.play()
        }
    }

    override fun pause() {
        if (musicServiceBound) {
            musicService.pause()
        }
    }

    override fun next() {

    }

    override fun previous() {
    }

    override fun onDestroy() {
        requireActivity().unbindService(serviceConnection)
        super.onDestroy()
    }


}