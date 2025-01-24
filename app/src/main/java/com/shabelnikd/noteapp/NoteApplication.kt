package com.shabelnikd.noteapp

import android.app.Application
import com.shabelnikd.noteapp.utils.PreferenceHelper
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class NoteApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = PreferenceHelper()
        sharedPreferences.initialize(this)
        Dependencies.init(this)
    }
}