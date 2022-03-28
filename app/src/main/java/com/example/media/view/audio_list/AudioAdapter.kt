package com.example.media.view.audio_list

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.UriLoader
import com.example.media.R
import com.example.media.data.model.MusicFile
import com.example.media.data.model.VideoFile
import com.example.media.databinding.AudioCardBinding
import com.example.media.utility.toDurationFormat
import kotlin.time.measureTimedValue

class AudioAdapter(
    private val contentResolver: ContentResolver,
    private val onItemClicked: (MusicFile) -> Unit
) :
    RecyclerView.Adapter<AudioAdapter.ViewHolder>() {

    private val dataList = ArrayList<MusicFile>()

    inner class ViewHolder(
        val binding: AudioCardBinding,
        private val onItemClicked: (MusicFile) -> Unit
    ) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        init {
            binding.root.setOnClickListener(this)
        }

        fun bindData(musicFile: MusicFile) {
            binding.audioTitle.text = musicFile.name
            binding.audioImage.setBackgroundColor(binding.root.resources.getColor(R.color.blue))
            binding.audioArtist.text = musicFile.artistName
            binding.audioDuration.text = "\u00b7 ${musicFile.duration.toDurationFormat()} \u00b7"
        }

        override fun onClick(p0: View?) {
            onItemClicked(dataList[absoluteAdapterPosition])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AudioCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onItemClicked)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(dataList[position])
    }

    override fun getItemCount() = dataList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateDataList(value: List<MusicFile>) {
        dataList.clear()
        dataList.addAll(value)
        notifyDataSetChanged()
    }
}