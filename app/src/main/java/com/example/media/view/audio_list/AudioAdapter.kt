package com.example.media.view.audio_list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.media.R
import com.example.media.data.model.MusicFile
import com.example.media.data.model.VideoFile
import com.example.media.databinding.AudioCardBinding
import com.example.media.utility.toDurationFormat
import kotlin.time.measureTimedValue

class AudioAdapter(private val onItemClicked: (MusicFile) -> Unit) :
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
            Glide.with(binding.root.context).load(musicFile.thumbnail)
                .error(R.drawable.ic_baseline_music_note_24)
                .centerCrop()
                .into(binding.audioImage)
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