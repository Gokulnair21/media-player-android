package com.example.media.view.audio_list

import android.app.Application
import android.content.ContentUris
import android.media.MediaMetadataRetriever
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.media.data.model.MusicFile
import com.example.media.data.model.VideoFile
import com.example.media.utility.Resource

class AudioListPageViewModel(application: Application) : AndroidViewModel(application) {


    val audios = MutableLiveData<Resource<List<MusicFile>>>()

    private val contentResolver by lazy {
        getApplication<Application>().contentResolver
    }

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
                            contentResolver.loadThumbnail(albumArtUri, Size(100, 100), null)
                        } catch (e: Exception) {
                            null
                        }
                    } else {
                        null
                    }
                    dataList.add(
                        MusicFile(
                            name,
                            fileUri,
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

    }


}