package com.example.kotlinomnicure.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.databinding.ItemMessageBinding

class MessageViewHolder(binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {
    private var binding: ItemMessageBinding? = binding

    fun getBinding(): ItemMessageBinding? {
        return binding
    }

    fun setBinding(binding: ItemMessageBinding?) {
        this.binding = binding
    }
}
