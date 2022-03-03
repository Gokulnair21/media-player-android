package com.example.media.data.model

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaFile(
    val name: String,
    val fileUri: Uri,
    val duration: Long,
    val thumbnail: Bitmap? = null
) : Parcelable