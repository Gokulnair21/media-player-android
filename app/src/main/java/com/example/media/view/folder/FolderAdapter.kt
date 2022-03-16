package com.example.media.view.folder

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.media.R
import com.example.media.data.model.Folder
import com.example.media.databinding.FolderCardBinding


class FolderAdapter(private val onItemClicked: (Folder) -> Unit) :
    RecyclerView.Adapter<FolderAdapter.ViewHolder>() {

    private val data = ArrayList<Folder>()

    inner class ViewHolder(
        private val binding: FolderCardBinding,
        private val onItemClicked: (Folder) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        fun bindData(folder: Folder) {
            binding.folderName.text = folder.name
            binding.folderSize.text = binding.root.context.getString(R.string.number_of_videos,folder.videoData.size)
        }

        override fun onClick(p0: View?) {
            onItemClicked(data[absoluteAdapterPosition])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FolderCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onItemClicked)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(data[position])
    }

    override fun getItemCount() = data.size


    @SuppressLint("NotifyDataSetChanged")
    fun updateData(value: List<Folder>) {
        data.clear()
        data.addAll(value)
        notifyDataSetChanged()
    }
}