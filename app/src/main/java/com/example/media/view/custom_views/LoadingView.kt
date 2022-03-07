package com.example.media.view.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.media.R

class LoadingView : LinearLayout {

    private lateinit var loadingMessageTextView: TextView

    var loadingMessage: String
        get() = loadingMessageTextView.text.toString()
        set(value) {
            loadingMessageTextView.text = value
        }


    private fun init(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.loading, this)
        loadingMessageTextView = findViewById(R.id.loadingMessage)
    }

    private fun setInitialData(context: Context, attrs: AttributeSet) {
        val arr = context.obtainStyledAttributes(attrs, R.styleable.LoadingView)
        arr.getString(R.styleable.LoadingView_loadingMessage)?.let {
            loadingMessage = it
        }
        arr.getBoolean(R.styleable.LoadingView_showLoadingMessage, true).apply {
            loadingMessageTextView.isVisible = this
        }
        arr.recycle()
    }

    constructor(context: Context) : super(context) {
        init(context)
    }


    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init(context)
        setInitialData(context, attributeSet)

    }

}