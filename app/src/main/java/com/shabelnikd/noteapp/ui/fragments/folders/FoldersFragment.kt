package com.shabelnikd.noteapp.ui.fragments.folders

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.shabelnikd.noteapp.R
import com.shabelnikd.noteapp.adapters.FoldersAdapter
import com.shabelnikd.noteapp.databinding.FragmentFoldersBinding
import com.shabelnikd.noteapp.models.Folder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FoldersFragment : Fragment() {

    private var _binding: FragmentFoldersBinding? = null
    private val binding get() = _binding!!

    private val foldersAdapter = FoldersAdapter()
    private val viewModel: FolderViewModel by viewModels()

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

        binding.rvFolders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = foldersAdapter
        }

    }

    private fun loadData() {
        viewModel.allFolders.observe(viewLifecycleOwner) { folders ->
            foldersAdapter.submitList(folders)
        }
    }

    private fun setupListeners() {
        with(binding) {
            btnAddFolder.setOnClickListener {
                showAlertNewFolder()
            }

        }

        binding.btnAllNotes.setOnClickListener {
            viewModel.selectFolder(-1)
            findNavController().popBackStack()
        }

    }

    @SuppressLint("InflateParams")
    private fun showAlertNewFolder() {
        val dialogView = AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.alert_add_folder, null)
        val dialog = dialogView.setView(view).create()

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(R.color.bg_alert_dialog)

        view.findViewById<AppCompatButton>(R.id.btnNewFolder).setOnClickListener {
            viewModel.insertNewFolder(
                Folder(view.findViewById<EditText>(R.id.etNewFolderName).text.toString())
                    .toFolderEntity())

            dialog.dismiss()

        }

        view.findViewById<AppCompatButton>(R.id.btnNewFolderCancel).setOnClickListener {
            dialog.dismiss()
        }


    }
}