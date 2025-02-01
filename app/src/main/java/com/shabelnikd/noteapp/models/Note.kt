package com.shabelnikd.noteapp.models

import com.shabelnikd.noteapp.database.entities.NoteEntity

data class Note(
    val title: String,
    val text: String,
    val colorHex: String,
    val folderId: Long? = null,
    val createdAt: String
) {
    fun toNoteEntity(): NoteEntity = NoteEntity(
        id = 0,
        text = text,
        folderId = folderId,
        title = title,
        colorHex = colorHex,
        createdAt = createdAt,
        isNoteDeleted = false
    )
}
