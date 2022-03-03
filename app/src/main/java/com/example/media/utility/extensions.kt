package com.example.media.utility

import com.example.media.data.model.Folder

fun ArrayList<Folder>.containsBucket(name: String): Int {
    this.forEachIndexed { index, folder ->
        if (folder.name == name) {
            return index
        }

    }
    return -1
}