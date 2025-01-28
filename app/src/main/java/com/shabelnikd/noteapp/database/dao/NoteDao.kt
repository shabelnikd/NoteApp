package com.shabelnikd.noteapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
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
        "SELECT notes.id, notes.folder_id, title, text, colorHex, createdAt, isNoteDeleted FROM notes WHERE isNoteDeleted = 0"
    )
    fun getAllNotes(): LiveData<List<NoteTuple>>

    @Query(
        "SELECT notes.id, notes.folder_id, title, text, colorHex, createdAt, isNoteDeleted FROM notes WHERE isNoteDeleted = 1"
    )
    fun getAllDeletedNotes(): LiveData<List<NoteTuple>>

    @Query("DELETE FROM notes WHERE id = :noteId")
    fun deleteNoteById(noteId: Long)

    @Query("DELETE FROM folders WHERE id = :folderId")
    fun deleteFolderById(folderId: Long)

    @Query("UPDATE notes SET isNoteDeleted = 1 WHERE id =:noteId")
    fun softDeleteNoteById(noteId: Long)

    @Query("UPDATE notes SET isNoteDeleted = 0 WHERE id =:noteId")
    fun restoreNoteById(noteId: Long)

    @Query("UPDATE notes SET colorHex = :colorHex WHERE id =:noteId")
    fun changeColorById(noteId: Long, colorHex: String)

    @Insert(entity = FolderEntity::class)
    fun insertNewFolder(folder: FolderEntity)

    @Update(entity = NoteEntity::class, onConflict = OnConflictStrategy.Companion.REPLACE)
    fun updateNote(note: NoteTuple)


    @Query(
        "SELECT folders.id, folder_name FROM folders"
    )
    fun getAllFolders(): LiveData<List<FolderTuple>>


    @Query(
        "SELECT notes.id, notes.folder_id, title, text, colorHex, createdAt, isNoteDeleted FROM notes " +
                "WHERE notes.folder_id = :folderId AND isNoteDeleted = 0"
    )
    fun getNotesByFolderId(folderId: Long): LiveData<List<NoteTuple>>

    @Query(
        "SELECT notes.id, notes.folder_id, title, text, colorHex, createdAt, isNoteDeleted FROM notes " +
                "WHERE notes.id = :noteId"
    )
    fun getNoteById(noteId: Long): LiveData<NoteTuple>


    @Query(
        "SELECT folders.id, folder_name FROM folders WHERE folders.id = :folderId"
    )
    fun getFolderById(folderId: Long): LiveData<FolderTuple>


}