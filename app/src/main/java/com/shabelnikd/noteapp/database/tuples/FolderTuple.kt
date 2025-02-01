package com.shabelnikd.noteapp.database.tuples

import androidx.room.ColumnInfo

data class FolderTuple(
    val id: Long,
    @ColumnInfo(name = "folder_name") val folderName: String,
)