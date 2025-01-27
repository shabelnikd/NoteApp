package com.shabelnikd.noteapp.database

import androidx.lifecycle.LiveData
import com.shabelnikd.noteapp.database.dao.NoteDao
import com.shabelnikd.noteapp.database.entities.FolderEntity
import com.shabelnikd.noteapp.database.entities.NoteEntity
import com.shabelnikd.noteapp.database.tuples.FolderTuple
import com.shabelnikd.noteapp.database.tuples.NoteTuple
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NoteRepository(private val noteDao: NoteDao) {
    suspend fun insertNewNote(noteEntity: NoteEntity) {
        withContext(Dispatchers.IO) {
            noteDao.insertNewNote(noteEntity)
        }
    }

    suspend fun getAllNotes(): LiveData<List<NoteTuple>> {
        return withContext(Dispatchers.IO) {
            return@withContext noteDao.getAllNotes()
        }
    }

    suspend fun deleteNoteById(noteId: Long) {
        withContext(Dispatchers.IO) {
            noteDao.deleteNoteById(noteId)
        }
    }


    suspend fun insertNewFolder(folderEntity: FolderEntity) {
        withContext(Dispatchers.IO) {
            noteDao.insertNewFolder(folderEntity)
        }
    }

    suspend fun getAllFolders(): LiveData<List<FolderTuple>> {
        return withContext(Dispatchers.IO) {
            return@withContext noteDao.getAllFolders()
        }
    }

    suspend fun getNotesByFolderId(folderId: Long): LiveData<List<NoteTuple>> {
        return withContext(Dispatchers.IO) {
            return@withContext noteDao.getNotesByFolderId(folderId)
        }
    }

    suspend fun getNoteById(noteId: Long): LiveData<NoteTuple> {
        return withContext(Dispatchers.IO) {
            return@withContext noteDao.getNoteById(noteId)
        }
    }

    suspend fun updateNote(note: NoteTuple) {
        return withContext(Dispatchers.IO) {
            noteDao.updateNote(note)
        }
    }

    suspend fun getFolderById(folderId: Long): LiveData<FolderTuple>  {
        return withContext(Dispatchers.IO) {
            return@withContext noteDao.getFolderById(folderId)
        }
    }


}