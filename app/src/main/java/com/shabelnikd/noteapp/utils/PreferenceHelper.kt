package com.shabelnikd.noteapp.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper {
    private var _sharedPreferences: SharedPreferences? = null
    private val sharedPreferences get() = _sharedPreferences!!

    fun initialize(context: Context) {
        _sharedPreferences = context.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
    }

    var isFirstLaunch: Boolean
        get() = sharedPreferences.getBoolean("isFirstLaunch", true)
        set(value) = sharedPreferences.edit().putBoolean("isFirstLaunch", value).apply()

    var layoutManager: Boolean
        get() = sharedPreferences.getBoolean("layoutManager", true)
        set(value) = sharedPreferences.edit().putBoolean("layoutManager", value).apply()

    var openedFolder: Int
        get() = sharedPreferences.getInt("openedFolder", 0)
        set(value) = sharedPreferences.edit().putInt("openedFolder", value).apply()
}