package com.shabelnikd.noteapp.ui.fragments

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.shabelnikd.noteapp.Dependencies
import com.shabelnikd.noteapp.adapters.NoteAdapter
import com.shabelnikd.noteapp.database.tuples.NoteTuple
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

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res)
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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
        if (FirebaseAuth.getInstance().currentUser == null) createSignInIntent()
        setupOrChangeLayout()
        checkData()
        setupListeners()
    }


    private fun setupOrChangeLayout() {
        when (sharedPreferences.layoutManager) {
            true -> {
                noteGridAdapter.setOnNoteClickListener { noteId ->
                    navigateToChangeNote(noteId)
                }
                binding.rvNotes.layoutManager = GridLayoutManager(requireContext(), 2)
                binding.rvNotes.adapter = noteGridAdapter
                binding.layoutChanger.isActivated = sharedPreferences.layoutManager
            }

            false -> {
                noteListAdapter.setOnNoteClickListener { noteId ->
                    navigateToChangeNote(noteId)
                }
                binding.rvNotes.layoutManager = LinearLayoutManager(requireContext())
                binding.rvNotes.adapter = noteListAdapter
                binding.layoutChanger.isActivated = sharedPreferences.layoutManager
            }
        }
    }

    private fun checkData(query: String = "") {
        binding.tvNotes.text = ""

        viewModel.currentFolderId.observe(viewLifecycleOwner) { folderId ->
            when (folderId) {
                -1L -> {
                    lifecycleScope.launch {
                        binding.tvNotes.text = "Все заметки"
                        filterByEditText(
                            Dependencies.noteRepository.getAllNotes(), query
                        ).observe(viewLifecycleOwner) { notes ->
                            noteListAdapter.submitList(notes)
                            noteGridAdapter.submitList(notes)
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
                            noteListAdapter.submitList(notes)
                            noteGridAdapter.submitList(notes)
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


    private fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )

        val signInIntent =
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers)
                .build()
        signInLauncher.launch(signInIntent)
    }


    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            Snackbar.make(binding.root, "Ваша аккаунт ${response?.email}", 1000).show()
        } else {
            Snackbar.make(binding.root, "Неудачная попытка", 1000).show()
        }
    }

}