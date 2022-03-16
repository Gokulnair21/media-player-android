package com.example.media.data.model

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MusicFile(
    val name: String,
    val fileUri: Uri,
    val duration: Long,
    val artistName: String? = null,
    val thumbnail: Bitmap? = null
) : Parcelable