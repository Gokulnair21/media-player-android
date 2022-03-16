package com.example.media.view.audio_list

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.media.data.model.MusicFile
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource

class AudioControlViewModel(application: Application) : AndroidViewModel(application) {

    private val exoPlayer by lazy {
        ExoPlayer.Builder(application.applicationContext).build()
    }
    val musicFileLiveData = MutableLiveData<MusicFile>()

    fun loadMusicFile(context: Context, musicFile: MusicFile) {
        musicFileLiveData.postValue(musicFile)
        if (exoPlayer.isPlaying) {
            stop()
        }
        exoPlayer.setMediaSource(createMediaSource(musicFile))
        exoPlayer.prepare()
        exoPlayer.playWhenReady=true


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


    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
    }

    private fun createMediaSource(musicFile: MusicFile): MediaSource {
        val dataSource = DefaultDataSource.Factory(getApplication<Application>().applicationContext)
        return ProgressiveMediaSource.Factory(dataSource)
            .createMediaSource(MediaItem.fromUri(musicFile.fileUri))
    }
}