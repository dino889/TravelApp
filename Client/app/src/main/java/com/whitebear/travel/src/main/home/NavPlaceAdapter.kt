package com.whitebear.travel.src.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.whitebear.travel.R
import com.whitebear.travel.databinding.ItemPlaceNavBinding
import com.whitebear.travel.src.dto.Place

class NavPlaceAdapter() : RecyclerView.Adapter<NavPlaceAdapter.NavViewHolder>(){
    var list = mutableListOf<Place>()
    inner class NavViewHolder(private val binding:ItemPlaceNavBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(place:Place){
            binding.place = place
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavViewHolder {
        return NavViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_place_nav,parent,false))

    }

    override fun onBindViewHolder(holder: NavViewHolder, position: Int) {
        holder.apply {
            bind(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}