package com.example.media.view.folder

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.media.R
import com.example.media.base.BaseFragment
import com.example.media.databinding.FolderPageFragmentBinding
import com.example.media.utility.Resource

class FolderPage : BaseFragment<FolderPageFragmentBinding>() {

    private val viewModel by viewModels<FolderPageViewModel>()

    private val folderAdapter = FolderAdapter {
        val action = FolderPageDirections.actionFolderPageToVideoListPage(
            it.mediaData.toTypedArray(),
            it.name
        )
        findNavController().navigate(action)
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FolderPageFragmentBinding.inflate(inflater, container, false)

    override fun setUpViews() {
        setHasOptionsMenu(true)
        binding.folderList.adapter = folderAdapter
    }

    override fun observeData() {
        viewModel.folder.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> {
                    binding.loading.isVisible = true
                    binding.folderList.isVisible = false
                }
                is Resource.Success -> {
                    state.data?.let { folderAdapter.updateData(it) }
                    binding.loading.isVisible = false
                    binding.folderList.isVisible = true
                }
                is Resource.Error -> {
                    showSnackBar(state.error.toString())
                }

            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val popUpMenu = PopupMenu(requireContext(), requireView())
        val popMenuInflater = popUpMenu.menuInflater
        popMenuInflater.inflate(R.menu.folder_page_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.refresh -> {
                viewModel.loadAllVideoFolders()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}