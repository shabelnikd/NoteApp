package com.shabelnikd.noteapp.ui.fragments

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
import com.shabelnikd.noteapp.databinding.FragmentHomeBinding
import com.shabelnikd.noteapp.models.ColorsEnum
import com.shabelnikd.noteapp.models.Note
import com.shabelnikd.noteapp.utils.PreferenceHelper
import dagger.hilt.android.AndroidEntryPoint
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
        initialize()
        setupOrChangeLayout()
        checkData()
        setupListeners()
    }

    private fun checkData() {
        viewModel.currentFolderId.observe(viewLifecycleOwner) { folderId ->
            when (folderId) {
                -1L -> {
                    viewModel.allNotes.observe(viewLifecycleOwner) { notes ->
                        noteListAdapter.submitList(notes)
                        noteGridAdapter.submitList(notes)
                    }
                }

                else -> {
                    lifecycleScope.launch {
                        Dependencies.noteRepository.getNotesByFolderId(folderId)
                            .observe(viewLifecycleOwner) { notes ->
                                noteListAdapter.submitList(notes)
                                noteGridAdapter.submitList(notes)
                            }
                    }

                }
            }
        }

    }

    private fun initialize() {
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


    private fun setupListeners() {

        binding.btnAddNote.setOnClickListener {

        }

        binding.layoutChanger.setOnClickListener {
            sharedPreferences.layoutManager = !sharedPreferences.layoutManager
            setupOrChangeLayout()
        }

        binding.btnOpenFolders.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFoldersFragment())
        }
    }

    private fun setupOrChangeLayout() {
        if (sharedPreferences.layoutManager) {
            binding.rvNotes.layoutManager = GridLayoutManager(requireContext(), 2)
            binding.rvNotes.adapter = noteGridAdapter
        } else {
            binding.rvNotes.layoutManager = LinearLayoutManager(requireContext())
            binding.rvNotes.adapter = noteListAdapter
        }
    }


}