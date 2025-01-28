package com.shabelnikd.noteapp.ui.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.shabelnikd.noteapp.Dependencies
import com.shabelnikd.noteapp.services.FolderService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    folderService: FolderService
) : ViewModel() {

    val currentFolderId: LiveData<Long> = folderService.currentFolderId.asLiveData()

    fun deleteNote(noteId: Long) {
        viewModelScope.launch {
            Dependencies.noteRepository.deleteNoteById(noteId)
        }
    }

    fun restoreNote(noteId: Long) {
        viewModelScope.launch {
            Dependencies.noteRepository.restoreNoteById(noteId)
        }
    }

    fun softDeleteNote(noteId: Long) {
        viewModelScope.launch {
            Dependencies.noteRepository.softDeleteNoteById(noteId)
        }
    }

}