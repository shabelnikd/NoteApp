package com.shabelnikd.noteapp.ui.fragments.folders

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shabelnikd.noteapp.Dependencies
import com.shabelnikd.noteapp.database.entities.FolderEntity
import com.shabelnikd.noteapp.database.entities.NoteEntity
import com.shabelnikd.noteapp.database.tuples.FolderTuple
import com.shabelnikd.noteapp.models.Note
import com.shabelnikd.noteapp.services.FolderService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val folderService: FolderService
) : ViewModel() {


    fun selectFolder(folderId: Long) {
        viewModelScope.launch {
            folderService.setCurrentFolderId(folderId)
        }
    }

    fun insertNewFolder(folderEntity: FolderEntity) {
        viewModelScope.launch {
            Dependencies.noteRepository.insertNewFolder(folderEntity)
        }
    }

}