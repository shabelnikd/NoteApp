package com.shabelnikd.noteapp.database.tuples

import androidx.room.ColumnInfo

data class NoteTuple(
    val id: Long,
    @ColumnInfo(name = "folder_id") val folderId: Long?,
    val title: String,
    val text: String,
    val colorHex: String,
    val createdAt: String,
)
