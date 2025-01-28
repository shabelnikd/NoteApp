package com.shabelnikd.noteapp.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.shabelnikd.noteapp.database.tuples.NoteTuple
import com.shabelnikd.noteapp.databinding.ItemNoteGridBinding
import com.shabelnikd.noteapp.databinding.ItemNoteListBinding
import com.shabelnikd.noteapp.models.ColorsEnum

class NoteAdapter(
    val layoutManager: Boolean
) : ListAdapter<NoteTuple, NoteAdapter.ViewHolder>(NoteDiffCallback()) {

    private var onNoteClick: ((noteId: Long) -> Unit)? = null
    private var onLongNoteClick: ((noteId: Long) -> Unit)? = null

    fun setOnNoteClickListener(listener: (noteId: Long) -> Unit) {
        this.onNoteClick = listener
    }

    fun setOnLongNoteClickListener(listener: (noteId: Long) -> Unit) {
        this.onLongNoteClick = listener
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
                true -> with(bindingGrid) {
                    tvNoteText.text = note.text
                    tvTitle.text = note.title
                    tvNoteCreated.text = note.createdAt.replace('|', ' ')

                    gridCard.setCardBackgroundColor(Color.parseColor(note.colorHex))

                    if (0xFFFFFF and gridCard.cardBackgroundColor.defaultColor
                        != 0xFFFFFF and Color.parseColor(ColorsEnum.DEFAULT.colorHex)) {
                        listOf(tvTitle, tvNoteText, tvNoteCreated).forEach { it.setTextColor(Color.BLACK) }
                    }
                }

                else -> with(bindingList) {
                    tvNoteText.text = note.text
                    tvTitle.text = note.title

                    listCard.setCardBackgroundColor(Color.parseColor(note.colorHex))

                    tvNoteCreated.text = note.createdAt.replace('|', ' ')
                    if (0xFFFFFF and listCard.cardBackgroundColor.defaultColor
                        != 0xFFFFFF and Color.parseColor(ColorsEnum.DEFAULT.colorHex)) {
                        listOf(tvTitle, tvNoteText, tvNoteCreated).forEach { it.setTextColor(Color.BLACK) }
                    }
                }
            }

            holder.itemView.setOnClickListener {
                onNoteClick?.invoke(note.id)
            }

            holder.itemView.setOnLongClickListener() {
                onLongNoteClick?.invoke(note.id)
                true
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