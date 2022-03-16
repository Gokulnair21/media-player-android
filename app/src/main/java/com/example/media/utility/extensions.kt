package com.example.media.utility

import com.example.media.data.model.Folder
import java.util.concurrent.TimeUnit

fun ArrayList<Folder>.containsBucket(name: String): Int {
    this.forEachIndexed { index, folder ->
        if (folder.name == name) {
            return index
        }

    }
    return -1
}

fun Long.toDurationFormat(): String {
    val zero = 0.toLong()
    val hour = TimeUnit.MILLISECONDS.toHours(this)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this) - TimeUnit.HOURS.toMinutes(
        hour
    )
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(
        TimeUnit.MILLISECONDS.toMinutes(this)
    )
    return if (hour == zero) {
        String.format("%02d:%02d", minutes, seconds)
    } else {
        String.format("%02d:%02d:%02d", hour, minutes, seconds)
    }
}