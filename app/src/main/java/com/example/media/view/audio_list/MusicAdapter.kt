package com.example.media.view.audio_list

import android.app.PendingIntent
import android.graphics.Bitmap
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager

class MusicAdapter:PlayerNotificationManager.MediaDescriptionAdapter {
    override fun getCurrentContentTitle(player: Player): CharSequence {
        TODO("Not yet implemented")
    }

    override fun createCurrentContentIntent(player: Player): PendingIntent? {
        TODO("Not yet implemented")
    }

    override fun getCurrentContentText(player: Player): CharSequence? {
        TODO("Not yet implemented")
    }

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? {
        TODO("Not yet implemented")
    }
}