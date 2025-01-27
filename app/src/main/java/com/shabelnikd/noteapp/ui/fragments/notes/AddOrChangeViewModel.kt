package com.shabelnikd.noteapp.ui.fragments.notes

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.shabelnikd.noteapp.Dependencies
import com.shabelnikd.noteapp.database.entities.NoteEntity
import com.shabelnikd.noteapp.services.FolderService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddOrChangeViewModel @Inject constructor(
    folderService: FolderService
) : ViewModel() {

    val currentFolderId: LiveData<Long> = folderService.currentFolderId.asLiveData()


    fun insertNote(note: NoteEntity) {
        viewModelScope.launch {
            Dependencies.noteRepository.insertNewNote(note)
        }
    }

}