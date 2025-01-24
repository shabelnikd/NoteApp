package com.shabelnikd.noteapp.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "folders", indices = [Index("id")],

    )
data class FolderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "folder_name") val folderName: String,
)
