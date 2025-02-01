package com.shabelnikd.noteapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shabelnikd.noteapp.database.tuples.FolderTuple
import com.shabelnikd.noteapp.databinding.ItemFolderBinding

class FoldersAdapter(
) : ListAdapter<FolderTuple, FoldersAdapter.ViewHolder>(GenericDiffUtil<FolderTuple>()) {

    private var onFolderClick: ((folderId: Long) -> Unit)? = null
    private var onFolderLongClick: ((folderId: Long) -> Unit)? = null

    fun setOnFolderClickListener(listener: (folderId: Long) -> Unit) {
        this.onFolderClick = listener
    }

    fun setOnFolderLongClickListener(listener: (folderId: Long) -> Unit) {
        this.onFolderLongClick = listener
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val folder = getItem(position)

        with(holder) {
            binding.btnFolder.text = folder.folderName

            binding.btnFolder.setOnClickListener {
                onFolderClick?.invoke(folder.id)
            }
        }

        holder.itemView.setOnLongClickListener {
            onFolderLongClick?.invoke(folder.id)
            true
        }

    }

    class ViewHolder(val binding: ItemFolderBinding) : RecyclerView.ViewHolder(binding.root)

}