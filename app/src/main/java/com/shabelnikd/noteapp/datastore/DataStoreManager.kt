package com.shabelnikd.noteapp.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

object DataStoreManager {
    private const val DATA_STORE_NAME = "app_preferences"
    private val IS_FIRST_LAUNCH_KEY = booleanPreferencesKey("is_first_launch")

    private var _dataStore: DataStore<Preferences>? = null
    private val dataStore get() = _dataStore!!

    fun initialize(context: Context) {
        _dataStore = context.applicationContext.dataStore
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_NAME)

    suspend fun isFirstLaunch(): Boolean {
        return dataStore.data.first()[IS_FIRST_LAUNCH_KEY] != false
    }

    suspend fun setFirstLaunch(isFirstLaunch: Boolean) {
        dataStore.edit { prefs ->
            prefs[IS_FIRST_LAUNCH_KEY] = isFirstLaunch
        }
    }

}