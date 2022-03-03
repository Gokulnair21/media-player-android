package com.example.media.view.video_list

import android.annotation.SuppressLint
import android.media.browse.MediaBrowser
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.example.media.data.model.MediaFile
import com.example.media.databinding.VideoCardBinding

class VideoListAdapter(private val onItemClicked: (MediaFile) -> Unit) :
    RecyclerView.Adapter<VideoListAdapter.ViewHolder>() {

    private val data: ArrayList<MediaFile> = arrayListOf()

    inner class ViewHolder(
        private val binding: VideoCardBinding,
        private val onItemClicked: (MediaFile) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        fun bindData(videoItem: MediaFile) {
            binding.videoTitle.text = videoItem.name
            binding.videoDuration.text = videoItem.duration.toString()
            binding.thumbnail.setImageBitmap(videoItem.thumbnail)
        }

        override fun onClick(p0: View?) {
            onItemClicked(data[adapterPosition])
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
    fun updateData(list: List<MediaFile>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }
}