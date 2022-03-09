package com.example.media.view.video_list

import android.content.Intent
import android.icu.lang.UCharacter
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.media.base.BaseFragment
import com.example.media.data.model.MediaFile
import com.example.media.databinding.VideoListPageFragmentBinding
import com.example.media.utility.Constant
import com.example.media.view.player.PlayerPage

class VideoListPage : BaseFragment<VideoListPageFragmentBinding>() {


    private val videoListAdapter = VideoListAdapter { _, index ->
        val intent = Intent(requireContext(), PlayerPage::class.java)
        intent.putParcelableArrayListExtra(
            Constant.MEDIA_FILE,
           ArrayList( navArgs.mediaFileList.toList())
        )
        intent.putExtra(Constant.MEDIA_FILE_TO_BE_PLAYED, index)
        startActivity(intent)
    }

    private val navArgs by navArgs<VideoListPageArgs>()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = VideoListPageFragmentBinding.inflate(inflater, container, false)

    override fun setUpViews() {
        binding.videList.adapter = videoListAdapter
        videoListAdapter.updateData(navArgs.mediaFileList.toList())
    }


}