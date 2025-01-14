package com.shabelnikd.noteapp

import android.app.Application
import com.shabelnikd.noteapp.datastore.DataStoreManager

class NoteApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DataStoreManager.initialize(this)
    }
}