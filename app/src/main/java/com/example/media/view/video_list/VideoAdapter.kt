package com.example.media.view.video_list

import android.annotation.SuppressLint
import android.media.browse.MediaBrowser
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.example.media.data.model.MediaFile
import com.example.media.databinding.VideoCardBinding
import java.util.concurrent.TimeUnit

class VideoListAdapter(private val onItemClicked: (MediaFile, Int) -> Unit) :
    RecyclerView.Adapter<VideoListAdapter.ViewHolder>() {

    private val data: ArrayList<MediaFile> = arrayListOf()

    inner class ViewHolder(
        private val binding: VideoCardBinding,
        private val onItemClicked: (MediaFile, Int) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        fun bindData(videoItem: MediaFile) {
            binding.videoTitle.text = videoItem.name

            binding.videoDuration.text = convertDurationToTime(videoItem.duration)
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
    fun updateData(list: List<MediaFile>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    fun convertDurationToTime(duration: Long): String {
        val hour = TimeUnit.MILLISECONDS.toHours(duration)
        val minute = TimeUnit.MILLISECONDS.toMinutes(duration)
        val zero = 0.toLong()
        val second = TimeUnit.MILLISECONDS.toSeconds(duration)
        return if (hour == zero) {
            if (minute == zero) {
                "00:$second"
            } else {
                "$minute:$second"
            }
        } else {
            "$hour:$minute:$second"
        }
    }
}