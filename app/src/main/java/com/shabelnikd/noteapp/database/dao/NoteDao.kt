package com.shabelnikd.noteapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.shabelnikd.noteapp.database.entities.FolderEntity
import com.shabelnikd.noteapp.database.entities.NoteEntity
import com.shabelnikd.noteapp.database.tuples.FolderTuple
import com.shabelnikd.noteapp.database.tuples.NoteTuple

@Dao
interface NoteDao {

    @Insert(entity = NoteEntity::class)
    fun insertNewNote(note: NoteEntity)

    @Query(
        "SELECT notes.id, notes.folder_id, title, text, colorHex, createdAt FROM notes"
    )
    fun getAllNotes(): List<NoteTuple>


    @Query("DELETE FROM notes WHERE id = :noteId")
    fun deleteNoteById(noteId: Long)


    @Insert(entity = FolderEntity::class)
    fun insertNewFolder(folder: FolderEntity)


    @Query(
        "SELECT folders.id, folder_name FROM folders"
    )
    fun getAllFolders(): List<FolderTuple>


    @Query(
        "SELECT notes.id, notes.folder_id, title, text, colorHex, createdAt FROM notes " +
                "WHERE notes.folder_id = :folderId"
    )
    fun getNotesByFolderId(folderId: Long): List<NoteTuple>


}