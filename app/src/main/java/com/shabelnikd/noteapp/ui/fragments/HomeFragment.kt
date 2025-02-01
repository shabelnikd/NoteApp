package com.shabelnikd.noteapp.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.shabelnikd.noteapp.ui.activities.MainActivity
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
        when (sharedPreferences.layoutManager) { // Если true то Grid Layout
            true -> with(binding) {
                noteGridAdapter.apply { // Применяем listener для Grid Layout
                    setOnNoteClickListener { noteId ->
                        navigateToChangeNote(noteId)
                    }

                    setOnLongNoteClickListener { noteId ->
                        showAlertNewFolderOrDeleteOrRestore(noteId)
                    }
                }

                rvNotes.apply {
                    layoutManager = GridLayoutManager(requireContext(), 2) // Применяем Grid Manager
                    adapter = noteGridAdapter
                }

                layoutChanger.isActivated = sharedPreferences.layoutManager // Меняем иконку layout
            }

            false -> with(binding) { // если false то Linear Layout
                noteListAdapter.apply { // Так же ставим слушатели
                    setOnNoteClickListener { noteId ->
                        navigateToChangeNote(noteId)
                    }

                    setOnLongNoteClickListener { noteId ->
                        showAlertNewFolderOrDeleteOrRestore(noteId)
                    }
                }

                rvNotes.apply {
                    layoutManager =
                        LinearLayoutManager(requireContext()) // И применяем Linear Layout
                    adapter = noteListAdapter
                }

                layoutChanger.isActivated = sharedPreferences.layoutManager
            }
        }
    }

    private fun checkData(query: String = "") { // Функция для назначения списка для адаптеров
        binding.tvNotes.text = ""

        val updateData = fun(list: List<NoteTuple>) {
            noteGridAdapter.submitList(list); noteListAdapter.submitList(list)
        } // Анонимная функция для обновления списков

        viewModel.currentFolderId.observe(viewLifecycleOwner) { folderId -> // Получаем текущую папку
            when (folderId) {
                -1L -> { // Если -1 то папка не выбрана
                    Log.d("ALLD", "checkData: folder not selected")
                    lifecycleScope.launch {
                        binding.tvNotes.text = "Все заметки"
                        filterByEditText(
                            Dependencies.noteRepository.getAllNotes(),
                            query // Получение всех заметок
                        ).observe(viewLifecycleOwner) { notes ->
                            updateData(notes.reversed())
                        }
                    }
                }

                -2L -> { // Если -2 то недавно удаленные
                    Log.e("ALLD", "checkData: recently deleted selected")
                    lifecycleScope.launch {
                        binding.tvNotes.text = "Недавно удаленные"
                        filterByEditText(
                            Dependencies.noteRepository.getAllDeletedNotes(),
                            query // Получение всех мягко удаленных заметок
                        ).observe(viewLifecycleOwner) { deletedNotes ->
                            updateData(deletedNotes.reversed())

                            // Переопределяем Click Listeners на окончательное удаление/восстановление удаленных заметок
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

                else -> { //Иначе папка выбрана
                    lifecycleScope.launch {
                        Dependencies.noteRepository.getFolderById(folderId)
                            // Получаем папку для отображения имени папкисверху экрана
                            .observe(viewLifecycleOwner) { folder ->
                                binding.tvNotes.text = folder.folderName
                                Log.d(
                                    "ALLD",
                                    "checkData: ${folder.folderName} with ${folder.id} is selected"
                                )
                            }

                        filterByEditText( // Вместе с функцией поиска получаем заметки по Id папки
                            Dependencies.noteRepository.getNotesByFolderId(folderId), query
                        ).observe(viewLifecycleOwner) { notes ->
                            updateData(notes.reversed())
                        }
                    }

                }
            }
        }
    }

    private fun filterByEditText( // Функция для фильтрации заметок по EditText
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

                searchScope.launch { // Задержка на 300 мл
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
//            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFoldersFragment())
            (activity as? MainActivity)?.openDrawer()
        }
    }

    private fun navigateToChangeNote(noteId: Long = -1) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToAddOrChangeNoteFragment(
                noteId
            )
        )
    }

    // Функция для отображения Alert
    private fun showAlertNewFolderOrDeleteOrRestore(noteId: Long = -1L, state: Boolean = false) {
        val bindingDeleteNote = AlertDeleteItemBinding.inflate(layoutInflater, null, false)
        val dialogView = AlertDialog.Builder(requireContext())

        val dialog = dialogView.setView(bindingDeleteNote.root).create()


        dialog.show()

        when (state) {
            false -> { // Если false то удаление
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

            true -> { // Если true на восстановление
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