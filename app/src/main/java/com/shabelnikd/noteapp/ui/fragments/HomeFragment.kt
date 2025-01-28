package com.shabelnikd.noteapp.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.shabelnikd.noteapp.Dependencies
import com.shabelnikd.noteapp.R
import com.shabelnikd.noteapp.adapters.NoteAdapter
import com.shabelnikd.noteapp.database.tuples.NoteTuple
import com.shabelnikd.noteapp.databinding.AlertDeleteItemBinding
import com.shabelnikd.noteapp.databinding.FragmentHomeBinding
import com.shabelnikd.noteapp.utils.PreferenceHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

//    private var _bindingDeleteNote: AlertDeleteItemBinding? = null
//    private val bindingDeleteNote get() = _bindingDeleteNote!!

    private val noteGridAdapter = NoteAdapter(true)
    private val noteListAdapter = NoteAdapter(false)

    private val sharedPreferences = PreferenceHelper()

    private val viewModel: HomeViewModel by viewModels()

    private val searchScope = CoroutineScope(Dispatchers.Main + SupervisorJob())


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        sharedPreferences.initialize(requireContext())
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOrChangeLayout()
        checkData()
        setupListeners()


    }


    private fun setupOrChangeLayout() {
        when (sharedPreferences.layoutManager) {
            true -> with(binding) {
                noteGridAdapter.apply {
                    setOnNoteClickListener { noteId ->
                        navigateToChangeNote(noteId)
                    }

                    setOnLongNoteClickListener { noteId ->
                        showAlertNewFolderOrDeleteOrRestore(noteId)
                    }
                }

                rvNotes.apply {
                    layoutManager = GridLayoutManager(requireContext(), 2)
                    adapter = noteGridAdapter
                }

                layoutChanger.isActivated = sharedPreferences.layoutManager
            }

            false -> with(binding) {
                noteListAdapter.apply {
                    setOnNoteClickListener { noteId ->
                        navigateToChangeNote(noteId)
                    }

                    setOnLongNoteClickListener { noteId ->
                        showAlertNewFolderOrDeleteOrRestore(noteId)
                    }
                }

                rvNotes.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = noteListAdapter
                }

                layoutChanger.isActivated = sharedPreferences.layoutManager
            }
        }
    }

    private fun checkData(query: String = "") {
        binding.tvNotes.text = ""

        val updateData = fun(list: List<NoteTuple>) {
            noteGridAdapter.submitList(list); noteListAdapter.submitList(list)
        }

        viewModel.currentFolderId.observe(viewLifecycleOwner) { folderId ->
            when (folderId) {
                -1L -> {
                    lifecycleScope.launch {
                        binding.tvNotes.text = "Все заметки"
                        filterByEditText(
                            Dependencies.noteRepository.getAllNotes(), query
                        ).observe(viewLifecycleOwner) { notes ->
                            updateData(notes.reversed())
                        }
                    }

                }

                -2L -> {
                    lifecycleScope.launch {
                        binding.tvNotes.text = "Недавно удаленные"
                        filterByEditText(
                            Dependencies.noteRepository.getAllDeletedNotes(), query
                        ).observe(viewLifecycleOwner) { deletedNotes ->
                            updateData(deletedNotes.reversed())
                            noteListAdapter.setOnLongNoteClickListener { noteId ->
                                showAlertNewFolderOrDeleteOrRestore(noteId, true)
                            }

                            noteGridAdapter.setOnLongNoteClickListener { noteId ->
                                showAlertNewFolderOrDeleteOrRestore(noteId, true)
                            }

                            noteListAdapter.setOnNoteClickListener { noteId ->
                                showAlertNewFolderOrDeleteOrRestore(noteId, true)
                            }

                            noteGridAdapter.setOnNoteClickListener { noteId ->
                                showAlertNewFolderOrDeleteOrRestore(noteId, true)
                            }
                        }

                    }
                }

                else -> {
                    lifecycleScope.launch {
                        Dependencies.noteRepository.getFolderById(folderId)
                            .observe(viewLifecycleOwner) { folder ->
                                binding.tvNotes.text = folder.folderName
                            }

                        filterByEditText(
                            Dependencies.noteRepository.getNotesByFolderId(folderId), query
                        ).observe(viewLifecycleOwner) { notes ->
                            updateData(notes.reversed())
                        }
                    }

                }
            }
        }
    }

    private fun filterByEditText(
        notesList: LiveData<List<NoteTuple>>,
        searchText: String
    ): LiveData<List<NoteTuple>> {
        return when (searchText.isEmpty()) {
            true -> notesList

            false -> notesList.map { notes ->
                notes.filter { note ->
                    note.text.contains(
                        binding.etSearchNotes.text.toString(),
                        ignoreCase = true
                    ) || note.title.contains(
                        binding.etSearchNotes.text.toString(),
                        ignoreCase = true
                    ) || note.createdAt.contains(
                        binding.etSearchNotes.text.toString(),
                        ignoreCase = true
                    )
                }
            }
        }
    }

    private fun setupListeners() {

        binding.etSearchNotes.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                searchScope.coroutineContext.cancelChildren()

                searchScope.launch {
                    delay(300)
                    checkData(p0.toString())
                }
            }
        })

        binding.btnAddNote.setOnClickListener {
            navigateToChangeNote()
        }

        binding.layoutChanger.setOnClickListener {
            sharedPreferences.layoutManager = !sharedPreferences.layoutManager
            setupOrChangeLayout()
        }

        binding.btnOpenFolders.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFoldersFragment())
        }
    }

    private fun navigateToChangeNote(noteId: Long = -1) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToAddOrChangeNoteFragment(
                noteId
            )
        )
    }

    private fun showAlertNewFolderOrDeleteOrRestore(noteId: Long = -1L, state: Boolean = false) {
        val bindingDeleteNote = AlertDeleteItemBinding.inflate(layoutInflater, null, false)
        val dialogView = AlertDialog.Builder(requireContext())

        val dialog = dialogView.setView(bindingDeleteNote.root).create()


        dialog.show()

        when (state) {
            false -> {
                with(bindingDeleteNote) {
                    tvAlertFolderOrNoteName.text = "Удалить заметку?"
                    btnDeleteFolderOrNote.setOnClickListener {
                        if (noteId != -1L) viewModel.softDeleteNote(noteId)
                        dialog.dismiss()
                    }

                    btnDeleteFolderOrNoteCancel.setOnClickListener {
                        dialog.dismiss()
                    }
                }
            }

            true -> {
                with(bindingDeleteNote) {
                    tvAlertFolderOrNoteName.text = "Восстановить заметку?"
                    btnDeleteFolderOrNote.text = "Восстановить"
                    btnDeleteFolderOrNoteCancel.text = "Удалить"

                    btnDeleteFolderOrNote.setOnClickListener {
                        if (noteId != -1L) viewModel.restoreNote(noteId)
                        dialog.dismiss()
                    }

                    btnDeleteFolderOrNoteCancel.setOnClickListener {
                        if (noteId != -1L) viewModel.deleteNote(noteId)
                        dialog.dismiss()
                    }
                }
            }
        }


        dialog.window?.setBackgroundDrawableResource(R.color.bg_alert_dialog)
        dialog.window?.container?.setBackgroundDrawableResource(R.color.bg_alert_dialog)


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}