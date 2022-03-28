package com.example.media.view.custom_views

import android.content.Context
import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.media.R
import com.google.android.exoplayer2.Player
import com.google.android.material.card.MaterialCardView

class AudioControllerView : MaterialCardView, View.OnClickListener {


    var listener: AudioControllerView.OnClickListener? = null

    var title: String
        get() = audioTitleTextView.text.toString()
        set(value) {
            audioTitleTextView.text = value
        }

    var artistName: String
        get() = audioArtistNameTextView.text.toString()
        set(value) {
            audioArtistNameTextView.text = value
        }

    var image: String?
        get() = null
        set(value) {
            val thumbnail = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    val uri = Uri.parse(value)
                    context.contentResolver.loadThumbnail(uri, Size(100, 100), null)
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
            if (thumbnail == null) {

            } else {
                Glide.with(context).load(thumbnail)
                    .centerCrop()
                    .into(imageView)
            }
        }


    var showPauseButton: Boolean
        get() = pauseImageButton.isVisible
        set(value) {
            pauseImageButton.isVisible = value
            playImageButton.isVisible = !value
        }


    var showPlayButton: Boolean
        get() = playImageButton.isVisible
        set(value) {
            playImageButton.isVisible = value
            pauseImageButton.isVisible = !value
        }

    private lateinit var playImageButton: ImageButton
    private lateinit var pauseImageButton: ImageButton

    private lateinit var previousImageButton: ImageButton
    private lateinit var nextImageButton: ImageButton

    private lateinit var audioTitleTextView: TextView
    private lateinit var audioArtistNameTextView: TextView
    lateinit var imageView: ImageView

    private fun init(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.audio_controller, this)
        playImageButton = findViewById(R.id.play)
        pauseImageButton = findViewById(R.id.pause)
        previousImageButton = findViewById(R.id.previous)
        nextImageButton = findViewById(R.id.next)
        audioTitleTextView = findViewById(R.id.title)
        audioArtistNameTextView = findViewById(R.id.artistName)
        imageView = findViewById(R.id.image)
        playImageButton.setOnClickListener(this)
        pauseImageButton.setOnClickListener(this)
        previousImageButton.setOnClickListener(this)
        nextImageButton.setOnClickListener(this)
    }

//    private fun setInitialData(context: Context, attrs: AttributeSet) {
//        val arr = context.obtainStyledAttributes(attrs, R.styleable.LoadingView)
//        arr.getString(R.styleable.LoadingView_loadingMessage)?.let {
//            loadingMessage = it
//        }
//        arr.getBoolean(R.styleable.LoadingView_showLoadingMessage, true).apply {
//            loadingMessageTextView.isVisible = this
//        }
//        arr.recycle()
//    }

    constructor(context: Context) : super(context) {
        init(context)
    }


    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init(context)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.pause -> {
                listener?.let {
                    playImageButton.isVisible = true
                    pauseImageButton.isVisible = false
                    it.pause()
                }


            }
            R.id.play -> {
                listener?.let {
                    playImageButton.isVisible = false
                    pauseImageButton.isVisible = true
                    it.play()
                }
            }
            R.id.next -> {
                listener?.next()
            }
            R.id.previous -> {
                listener?.previous()
            }
        }
    }

    interface OnClickListener {
        fun play()
        fun pause()
        fun next()
        fun previous()
    }
}