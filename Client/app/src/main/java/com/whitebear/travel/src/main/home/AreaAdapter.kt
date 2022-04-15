package com.whitebear.travel.src.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.whitebear.travel.R
import com.whitebear.travel.databinding.ItemAreaBinding
import com.whitebear.travel.src.dto.Area

class AreaAdapter() : RecyclerView.Adapter<AreaAdapter.AreaViewHolder>(){
    var list = mutableListOf<Area>()
    inner class AreaViewHolder(private val binding:ItemAreaBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(area:Area){
            binding.area = area
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AreaViewHolder {
        return AreaViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_area,parent,false))
    }

    override fun onBindViewHolder(holder: AreaViewHolder, position: Int) {
        holder.apply {
            bind(list[position])
            itemView.setOnClickListener{
                itemClickListener.onClick(it, position, list[position].name, list[position].id)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    interface ItemClickListener{
        fun onClick(view: View, position: Int, areaName: String,areaId:Int)
    }

    private lateinit var itemClickListener : ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }
}