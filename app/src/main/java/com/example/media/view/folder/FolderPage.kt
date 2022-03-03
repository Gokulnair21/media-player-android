package com.example.media.view.folder

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.media.R
import com.example.media.base.BaseFragment
import com.example.media.databinding.FolderPageFragmentBinding
import com.example.media.utility.Resource

class FolderPage : BaseFragment<FolderPageFragmentBinding>() {

    private val viewModel by viewModels<FolderPageViewModel>()

    private val folderAdapter = FolderAdapter {

    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FolderPageFragmentBinding.inflate(inflater, container, false)

    override fun setUpViews() {
        binding.folderList.adapter = folderAdapter
    }

    override fun observeData() {
        viewModel.folder.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    state.data?.let { folderAdapter.updateData(it) }
                }
                is Resource.Error -> {
                    showSnackBar(state.error.toString())
                }

            }
        }

    }

}