package com.sam.fetchweatherperiodically.view

import androidx.recyclerview.widget.RecyclerView
import com.sam.fetchweatherperiodically.databinding.HeaderBinding

class HeaderViewHolder(val binding: HeaderBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(text:String){
        binding.headerTitle.text = text
    }
}