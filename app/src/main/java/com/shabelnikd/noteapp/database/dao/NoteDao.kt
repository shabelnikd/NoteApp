package com.shabelnikd.noteapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.shabelnikd.noteapp.database.entities.FolderEntity
import com.shabelnikd.noteapp.database.entities.NoteEntity
import com.shabelnikd.noteapp.database.tuples.FolderTuple
import com.shabelnikd.noteapp.database.tuples.NoteTuple

@Dao
interface NoteDao {

    @Insert(entity = NoteEntity::class, onConflict = OnConflictStrategy.Companion.REPLACE)
    fun insertNewNote(note: NoteEntity)

    @Query(
        "SELECT notes.id, notes.folder_id, title, text, colorHex, createdAt FROM notes"
    )
    fun getAllNotes(): LiveData<List<NoteTuple>>


    @Query("DELETE FROM notes WHERE id = :noteId")
    fun deleteNoteById(noteId: Long)


    @Insert(entity = FolderEntity::class)
    fun insertNewFolder(folder: FolderEntity)


    @Query(
        "SELECT folders.id, folder_name FROM folders"
    )
    fun getAllFolders(): LiveData<List<FolderTuple>>


    @Query(
        "SELECT notes.id, notes.folder_id, title, text, colorHex, createdAt FROM notes " +
                "WHERE notes.folder_id = :folderId"
    )
    fun getNotesByFolderId(folderId: Long): LiveData<List<NoteTuple>>

    @Query(
        "SELECT notes.id, notes.folder_id, title, text, colorHex, createdAt FROM notes " +
                "WHERE notes.id = :noteId"
    )
    fun getNoteById(noteId: Long): LiveData<NoteTuple>

    @Update(entity = NoteEntity::class, onConflict = OnConflictStrategy.Companion.REPLACE)
    fun updateNote(note: NoteTuple)

    @Delete(entity = NoteEntity::class)
    fun deleteNote(note: NoteTuple)

    @Delete(entity = FolderEntity::class)
    fun deleteFolder(folder: FolderTuple)

    @Query(
        "SELECT folders.id, folder_name FROM folders WHERE folders.id = :folderId"
    )
    fun getFolderById(folderId: Long): LiveData<FolderTuple>


}