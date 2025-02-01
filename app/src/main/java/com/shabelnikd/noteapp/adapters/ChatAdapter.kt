package com.shabelnikd.noteapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shabelnikd.noteapp.databinding.ItemChatMessageBinding
import com.shabelnikd.noteapp.models.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter(
) : ListAdapter<Message, ChatAdapter.ViewHolder>(GenericDiffUtil<Message>()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemChatMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val message = getItem(position)
        holder.binding.tvMessage.text = message.message
        holder.binding.tvTime.text = "--:--"

        message.timestamp?.let { timestamp ->
            holder.binding.tvTime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp.seconds * 1000L))
        }


    }

    class ViewHolder(val binding: ItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root)

}