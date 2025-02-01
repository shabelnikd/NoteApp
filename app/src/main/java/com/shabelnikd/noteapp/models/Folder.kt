package com.shabelnikd.noteapp.models

import com.shabelnikd.noteapp.database.entities.FolderEntity

data class Folder(
    val folderName: String
) {
    fun toFolderEntity(): FolderEntity = FolderEntity(
        id = 0,
        folderName = folderName
    )
}
