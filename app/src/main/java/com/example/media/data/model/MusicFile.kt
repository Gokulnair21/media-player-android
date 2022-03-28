package com.example.media.data.model

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MusicFile(
    val name: String,

    @SerializedName("file_uri")
    @Expose
    val fileUri: String,
    val duration: Long,
    val artistName: String? = null,
    val thumbnail: String? = null
) : Parcelable