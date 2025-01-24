package com.shabelnikd.noteapp

import com.shabelnikd.noteapp.services.FolderService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFolderService(): FolderService {
        return FolderService()
    }
}