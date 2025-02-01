package com.shabelnikd.noteapp.services


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class FolderService @Inject constructor() {
    private val _currentFolderId = MutableStateFlow<Long>(-1)
    val currentFolderId: StateFlow<Long> = _currentFolderId.asStateFlow()

    fun setCurrentFolderId(folderId: Long) {
        _currentFolderId.value = folderId
    }

}