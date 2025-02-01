package com.shabelnikd.noteapp.ui.fragments.folders

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.shabelnikd.noteapp.Dependencies
import com.shabelnikd.noteapp.database.entities.FolderEntity
import com.shabelnikd.noteapp.services.FolderService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val folderService: FolderService
) : ViewModel() {

    val currentFolderId: LiveData<Long> = folderService.currentFolderId.asLiveData()

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

    fun deleteFolder(folderId: Long) {
        viewModelScope.launch {
            Dependencies.noteRepository.deleteFolderById(folderId)
        }
    }

}