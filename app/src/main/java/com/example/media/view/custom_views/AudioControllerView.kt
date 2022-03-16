package com.example.media.view.custom_views

import android.content.Context
import android.media.Image
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.media.R
import com.google.android.exoplayer2.Player
import com.google.android.material.card.MaterialCardView

class AudioControllerView : MaterialCardView, View.OnClickListener {


    var listener: AudioControllerView.OnClickListener? = null

    var title: String
        get() = audioTitleTextView.text.toString()
        set(value) {
            audioTitleTextView.text = title.toString()
        }

    val image get() = imageView

    private lateinit var playImageButton: ImageButton
    private lateinit var pauseImageButton: ImageButton

    private lateinit var previousImageButton: ImageButton
    private lateinit var nextImageButton: ImageButton

    private lateinit var audioTitleTextView: TextView
    private lateinit var imageView: ImageView

    private fun init(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.audio_controller, this)
        playImageButton = findViewById(R.id.play)
        pauseImageButton = findViewById(R.id.pause)
        previousImageButton = findViewById(R.id.previous)
        nextImageButton = findViewById(R.id.next)
        audioTitleTextView = findViewById(R.id.title)
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