package com.sam.fetchweatherperiodically.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sam.fetchweatherperiodically.databinding.HeaderBinding
import com.sam.fetchweatherperiodically.databinding.SubheadingBinding

class MultiTypeAdapter(var list : MutableList<Name>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        when(viewType){
            1->{
                val binding = HeaderBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                return HeaderViewHolder(binding)
            }
            else -> {
                val binding = SubheadingBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                return NormalViewHolder(binding)

            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when(list[position].type){
            1->{
                (holder as HeaderViewHolder).bind(list[position].name)
            }
            2->{
                (holder as NormalViewHolder).bind(list[position].name)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].type
    }

    fun setData(names: List<Name>) {
        list = names as MutableList<Name>
    }
}