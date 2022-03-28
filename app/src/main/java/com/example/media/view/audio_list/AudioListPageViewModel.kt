package com.example.media.view.audio_list

import android.app.Application
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaMetadataRetriever
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.media.R
import com.example.media.data.model.MusicFile
import com.example.media.data.model.VideoFile
import com.example.media.utility.Constant
import com.example.media.utility.Resource
import com.google.gson.Gson
import kotlinx.coroutines.flow.collect

class AudioListPageViewModel(
    private val contentResolver: ContentResolver,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {


    val audios = MutableLiveData<Resource<List<MusicFile>>>()
    val currentMusicFile = MutableLiveData<MusicFile?>()


    private val sharedPreferencesListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, _ -> insertMusicFile() }


    private fun loadAudioData() {
        try {
            audios.postValue(Resource.Loading())
            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Audio.Media.getContentUri(
                        MediaStore.VOLUME_EXTERNAL
                    )
                } else {
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Media.BUCKET_DISPLAY_NAME

                )
            val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"
            val query = contentResolver.query(
                collection,
                projection,
                null,
                null,
                sortOrder
            )
            query?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val durationColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val albumID = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
                val artistName = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)
                val dataList = ArrayList<MusicFile>()
                while (cursor.moveToNext()) {
                    val fileID = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val duration = cursor.getLong(durationColumn)
                    val fileUri = ContentUris.withAppendedId(collection, fileID)
                    val artist: String? = cursor.getString(artistName)
                    val thumbnail = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        try {
                            val albumIDValue = cursor.getLong(albumID)
                            val albumArtUri = ContentUris.withAppendedId(collection, albumIDValue)
                            albumArtUri.toString()

                        } catch (e: Exception) {
                            null
                        }
                    } else {
                        null
                    }
                    dataList.add(
                        MusicFile(
                            name,
                            fileUri.toString(),
                            duration,
                            thumbnail = thumbnail,
                            artistName = artist
                        )
                    )
                }
                audios.postValue(Resource.Success(dataList))
                cursor.close()
            }
        } catch (e: Exception) {
            audios.postValue(Resource.Error(e.message.toString()))
        }
    }


    init {
        loadAudioData()
        insertMusicFile()
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)
    }

    override fun onCleared() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferencesListener)
        super.onCleared()
    }

    private fun insertMusicFile() {
        sharedPreferences.getString(Constant.CURRENT_MUSIC_FILE, null)?.also {
            val gson = Gson()
            val musicFile = gson.fromJson(it, MusicFile::class.java)
            currentMusicFile.postValue(musicFile)
        } ?: currentMusicFile.postValue(null)

    }


    class Factory(
        private val contentResolver: ContentResolver,
        private val sharedPreferences: SharedPreferences
    ) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AudioListPageViewModel(contentResolver, sharedPreferences) as T
        }
    }


}