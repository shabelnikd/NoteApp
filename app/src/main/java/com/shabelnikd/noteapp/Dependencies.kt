package com.shabelnikd.noteapp

import android.content.Context
import androidx.room.Room
import com.shabelnikd.noteapp.database.AppDatabase
import com.shabelnikd.noteapp.database.NoteRepository

object Dependencies {

    private var _applicationContext: Context? = null
    private val applicationContext get() = _applicationContext!!

    fun init(context: Context) {
        _applicationContext = context
    }

    private val appDatabase: AppDatabase by lazy {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "database.db").build()
    }

    val noteRepository: NoteRepository by lazy { NoteRepository(appDatabase.getNoteDao()) }

}