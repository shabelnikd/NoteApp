package com.shabelnikd.noteapp.adapters

import androidx.recyclerview.widget.DiffUtil
import com.shabelnikd.noteapp.database.tuples.FolderTuple
import com.shabelnikd.noteapp.database.tuples.NoteTuple
import com.shabelnikd.noteapp.models.Message

class GenericDiffUtil<T: Any> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return when {
            oldItem is Message && newItem is Message -> oldItem.docId == newItem.docId
            oldItem is FolderTuple && newItem is FolderTuple -> oldItem.id == newItem.id
            oldItem is NoteTuple && newItem is NoteTuple -> oldItem.id == newItem.id
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return when {
            oldItem is Message && newItem is Message -> oldItem == newItem
            oldItem is FolderTuple && newItem is FolderTuple -> oldItem == newItem
            oldItem is NoteTuple && newItem is NoteTuple -> oldItem == newItem
            else -> false
        }
    }
}