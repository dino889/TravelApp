package com.whitebear.travel.src.main.route

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.whitebear.travel.R
import com.whitebear.travel.databinding.ItemRoutesPlaceListBinding
import com.whitebear.travel.src.dto.Place

class RouteDetailAdapter : RecyclerView.Adapter<RouteDetailAdapter.DetailViewHolder>() {
    var list = mutableListOf<Place>()
    inner class DetailViewHolder(private val binding:ItemRoutesPlaceListBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(place:Place){
            binding.place = place
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        return DetailViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_routes_place_list,parent,false))
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.apply {
            bind(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}