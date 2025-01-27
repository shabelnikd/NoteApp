package com.shabelnikd.noteapp.database.entities

import androidx.room.AutoMigration
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "notes",
    indices = [Index("id"), Index("folder_id")],
    foreignKeys = [
        ForeignKey(
            entity = FolderEntity::class,
            parentColumns = ["id"],
            childColumns = ["folder_id"]
        )
    ]

)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "folder_id") val folderId: Long?,
    val title: String,
    val text: String,
    @ColumnInfo(defaultValue = "#424242") val colorHex: String = "#424242",
    val createdAt: String,
)
