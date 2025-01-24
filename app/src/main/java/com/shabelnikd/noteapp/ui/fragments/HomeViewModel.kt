package com.shabelnikd.noteapp.ui.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.shabelnikd.noteapp.Dependencies
import com.shabelnikd.noteapp.database.tuples.NoteTuple
import com.shabelnikd.noteapp.services.FolderService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    folderService: FolderService
) : ViewModel() {

    val currentFolderId: LiveData<Long> = folderService.currentFolderId.asLiveData()

    private val _allNotes = MutableLiveData<List<NoteTuple>>()
    val allNotes: LiveData<List<NoteTuple>> = _allNotes

    private val _allNotesByFolderId = MutableLiveData<List<NoteTuple>>()
    val allNotesByFolderId: LiveData<List<NoteTuple>> = _allNotesByFolderId


    init {
        getAllNotes()
    }

    private fun getAllNotes() {
        viewModelScope.launch {
            _allNotes.value = Dependencies.noteRepository.getAllNotes()
        }
    }

    private fun getAllNotesByFolderId(folderId: Long) {
        viewModelScope.launch {
            _allNotesByFolderId.value = Dependencies.noteRepository.getNotesByFolderId(folderId)
        }
    }

}