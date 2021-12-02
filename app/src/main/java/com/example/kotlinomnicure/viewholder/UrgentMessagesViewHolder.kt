package com.example.kotlinomnicure.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinomnicure.databinding.ItemUrgentMessagesBinding

class UrgentMessagesViewHolder(binding: ItemUrgentMessagesBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private var binding: ItemUrgentMessagesBinding? = binding

    fun getBinding(): ItemUrgentMessagesBinding? {
        return binding
    }

    fun setBinding(binding: ItemUrgentMessagesBinding?) {
        this.binding = binding
    }
}
