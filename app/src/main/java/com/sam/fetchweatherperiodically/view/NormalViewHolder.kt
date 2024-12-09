package com.sam.fetchweatherperiodically.view

import androidx.recyclerview.widget.RecyclerView
import com.sam.fetchweatherperiodically.databinding.SubheadingBinding

class NormalViewHolder(val binding: SubheadingBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(text:String){
        binding.textView.text = text
    }
}