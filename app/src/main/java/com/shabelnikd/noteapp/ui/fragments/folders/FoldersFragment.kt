package com.shabelnikd.noteapp.ui.fragments.folders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.shabelnikd.noteapp.Dependencies
import com.shabelnikd.noteapp.R
import com.shabelnikd.noteapp.adapters.FoldersAdapter
import com.shabelnikd.noteapp.databinding.AlertAddFolderBinding
import com.shabelnikd.noteapp.databinding.AlertDeleteItemBinding
import com.shabelnikd.noteapp.databinding.FragmentFoldersBinding
import com.shabelnikd.noteapp.models.Folder
import com.shabelnikd.noteapp.ui.activities.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FoldersFragment : Fragment() {

    private var _binding: FragmentFoldersBinding? = null
    private val binding get() = _binding!!

//    private var _bindingAddFolder: AlertAddFolderBinding? = null
//    private val bindingAddFolder get() = _bindingAddFolder!!

//    private var _bindingDeleteFolder: AlertDeleteItemBinding? = null
//    private val bindingDeleteFolder get() = _bindingDeleteFolder!!

    private val foldersAdapter = FoldersAdapter()
    private val viewModel: FolderViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFoldersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        loadData()
        setupListeners()

    }

    private fun initialize() {
        foldersAdapter.setOnFolderClickListener { folderId ->
            viewModel.selectFolder(folderId)
            findNavController().popBackStack()
        }

        foldersAdapter.setOnFolderLongClickListener { folderId ->
            showAlertNewFolderOrDelete(false, folderId)
        }

        binding.rvFolders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = foldersAdapter
        }

    }

    private fun loadData() {
        lifecycleScope.launch {
            Dependencies.noteRepository.getAllFolders().observe(viewLifecycleOwner) { folders ->
                foldersAdapter.submitList(folders)
            }
        }

    }

    private fun setupListeners() {
        with(binding) {
            btnAddFolder.setOnClickListener {
                showAlertNewFolderOrDelete(true)
            }

        }
        binding.btnOpenDrawer.setOnClickListener {
            (activity as? MainActivity)?.openDrawer()
        }

        binding.btnAllNotes.setOnClickListener {
            viewModel.selectFolder(-1L)
            findNavController().popBackStack()
        }

        binding.btnDeletedNotes.setOnClickListener {
            viewModel.selectFolder(-2L)
            findNavController().popBackStack()
        }

    }

    private fun showAlertNewFolderOrDelete(state: Boolean, folderId: Long = -1L) {
        val dialogView = AlertDialog.Builder(requireContext())

        when (state) {
            true -> { //true is Add Folder
                val bindingAddFolder = AlertAddFolderBinding.inflate(layoutInflater, null, false)

                val dialog = dialogView.setView(bindingAddFolder.root).create()

                dialog.show()

                with(bindingAddFolder) {
                    btnNewFolder.setOnClickListener {
                        viewModel.insertNewFolder(
                            Folder(etNewFolderName.text.toString())
                                .toFolderEntity()
                        )
                        dialog.dismiss()
                    }

                    btnNewFolderCancel.setOnClickListener {
                        dialog.dismiss()
                    }
                }
                dialog.window?.setBackgroundDrawableResource(R.color.bg_alert_dialog)
                dialog.window?.container?.setBackgroundDrawableResource(R.color.bg_alert_dialog)
            }

            false -> { //false is DeleteFolder
                val bindingDeleteFolder =
                    AlertDeleteItemBinding.inflate(layoutInflater, null, false)
                val dialog = dialogView.setView(bindingDeleteFolder.root).create()

                dialog.show()

                with(bindingDeleteFolder) {
                    tvAlertFolderOrNoteName.text = "Удалить папку?"

                    btnDeleteFolderOrNote.setOnClickListener {
                        viewModel.currentFolderId.observe(viewLifecycleOwner) { currentFolderId ->
                            if (folderId == currentFolderId) viewModel.selectFolder(-1L)
                        }
                        if (folderId != -1L) viewModel.deleteFolder(folderId)

                        dialog.dismiss()
                    }

                    btnDeleteFolderOrNoteCancel.setOnClickListener {
                        dialog.dismiss()
                    }
                }
                dialog.window?.setBackgroundDrawableResource(R.color.bg_alert_dialog)
                dialog.window?.container?.setBackgroundDrawableResource(R.color.bg_alert_dialog)
            }
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}