package com.example.media.view.video_list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.media.data.model.VideoFile
import com.example.media.databinding.VideoCardBinding
import com.example.media.utility.toDurationFormat
import com.example.media.utility.toDurationFormat
import java.util.concurrent.TimeUnit

class VideoListAdapter(private val onItemClicked: (VideoFile, Int) -> Unit) :
    RecyclerView.Adapter<VideoListAdapter.ViewHolder>() {

    private val data: ArrayList<VideoFile> = arrayListOf()

    inner class ViewHolder(
        private val binding: VideoCardBinding,
        private val onItemClicked: (VideoFile, Int) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        fun bindData(videoItem: VideoFile) {
            binding.videoTitle.text = videoItem.name

            binding.videoDuration.text = videoItem.duration.toDurationFormat()
            binding.thumbnail.setImageBitmap(videoItem.thumbnail)
        }

        override fun onClick(p0: View?) {
            onItemClicked(data[absoluteAdapterPosition], absoluteAdapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = VideoCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onItemClicked)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(data[position])
    }

    override fun getItemCount() = data.size

    @SuppressLint("NotifyDataSetChanged")//Dataset is repopulated
    fun updateData(list: List<VideoFile>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }


}