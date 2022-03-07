package com.example.media.view.folder

import android.app.Application
import android.content.ContentUris
import android.database.Cursor
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.media.data.model.Folder
import com.example.media.data.model.MediaFile
import com.example.media.utility.Resource
import com.example.media.utility.containsBucket

class FolderPageViewModel(application: Application) : AndroidViewModel(application) {

    private val contentResolver by lazy {
        getApplication<Application>().contentResolver
    }
    val folder = MutableLiveData<Resource<List<Folder>>>()

     fun loadAllVideoFolders() {
        try {
            folder.postValue(Resource.Loading())
            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Video.Media.getContentUri(
                        MediaStore.VOLUME_EXTERNAL
                    )
                } else {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }
            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DATE_TAKEN,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION
            )
            val sortOrder = "${MediaStore.Video.Media.BUCKET_DISPLAY_NAME} ASC"
            val query = contentResolver.query(
                collection,
                projection,
                null,
                null,
                sortOrder
            )
            query?.use { cursor ->
                val bucketDisplayName =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
                val videoDisplayName =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val videoDuration =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val videoID = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val dataList = ArrayList<Folder>()
                while (cursor.moveToNext()) {
                    val bucketName = cursor.getString(bucketDisplayName)
                    val fileID = cursor.getLong(videoID)
                    val name = cursor.getString(videoDisplayName)
                    val fileUri = ContentUris.withAppendedId(collection, fileID)
                    val duration = cursor.getLong(videoDuration)
                    val thumbnail = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        contentResolver.loadThumbnail(fileUri, Size(2000, 100), null)
                    } else {
                        MediaStore.Video.Thumbnails.getThumbnail(
                            contentResolver, fileID,
                            MediaStore.Images.Thumbnails.MINI_KIND,
                            null as BitmapFactory.Options?
                        )
                    }
                    val index = dataList.containsBucket(bucketName)
                    val mediaFile = MediaFile(name, fileUri, duration, thumbnail)
                    if (index != -1) {
                        dataList[index].mediaData.add(mediaFile)
                    } else {
                        dataList.add(Folder(bucketName, arrayListOf(mediaFile)))
                    }
                }
                folder.postValue(Resource.Success(dataList))
            }
        } catch (e: Exception) {
            folder.postValue(Resource.Error(e.message))
        }
    }

    init {
        loadAllVideoFolders()
    }

}