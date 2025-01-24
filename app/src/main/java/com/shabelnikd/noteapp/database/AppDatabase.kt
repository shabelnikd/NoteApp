package com.shabelnikd.noteapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shabelnikd.noteapp.database.dao.NoteDao
import com.shabelnikd.noteapp.database.entities.FolderEntity
import com.shabelnikd.noteapp.database.entities.NoteEntity

@Database(
    version = 1,
    entities = [
        FolderEntity::class,
        NoteEntity::class
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getNoteDao(): NoteDao

}