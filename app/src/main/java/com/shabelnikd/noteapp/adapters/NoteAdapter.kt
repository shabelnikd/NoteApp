package com.shabelnikd.noteapp.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shabelnikd.noteapp.database.tuples.NoteTuple
import com.shabelnikd.noteapp.databinding.ItemNoteGridBinding
import com.shabelnikd.noteapp.databinding.ItemNoteListBinding

class NoteAdapter(
    val layoutManager: Boolean
) : ListAdapter<NoteTuple, NoteAdapter.ViewHolder>(NoteDiffCallback()) {

    private var onNoteClick: ((noteId: Long) -> Unit)? = null

    fun setOnNoteClickListener(listener: (noteId: Long) -> Unit) {
        this.onNoteClick = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val bindingGrid =
            ItemNoteGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val bindingList =
            ItemNoteListBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(
            bindingList = bindingList,
            bindingGrid = bindingGrid,
            layoutManager
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val note = getItem(position)

        with(holder) {
            when (layoutManager) {
                true -> {
                    bindingGrid.tvNoteText.text = note.text
                    bindingGrid.tvTitle.text = note.title
                    bindingGrid.tvNoteCreated.text = note.createdAt.replace('|', ' ')
                    bindingGrid.gridCard.setCardBackgroundColor(Color.parseColor(note.colorHex))
                }

                else -> {
                    bindingList.tvNoteText.text = note.text
                    bindingList.tvTitle.text = note.title
                    bindingList.tvNoteCreated.text = note.createdAt.replace('|', ' ')
                    bindingList.listCard.setCardBackgroundColor(Color.parseColor(note.colorHex))
                }
            }

            holder.itemView.setOnClickListener {
                onNoteClick?.invoke(note.id)
            }
        }

    }

    class ViewHolder(
        val bindingList: ItemNoteListBinding,
        val bindingGrid: ItemNoteGridBinding,
        layoutManager: Boolean
    ) :
        RecyclerView.ViewHolder(
            if (layoutManager) {
                bindingGrid.root
            } else {
                bindingList.root
            }
        )

    class NoteDiffCallback : DiffUtil.ItemCallback<NoteTuple>() {
        override fun areItemsTheSame(
            oldItem: NoteTuple,
            newItem: NoteTuple
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: NoteTuple,
            newItem: NoteTuple
        ): Boolean {
            return oldItem == newItem
        }
    }

}