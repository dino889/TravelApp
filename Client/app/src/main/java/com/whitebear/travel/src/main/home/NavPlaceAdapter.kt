package com.whitebear.travel.src.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.whitebear.travel.R
import com.whitebear.travel.databinding.ItemPlaceNavBinding
import com.whitebear.travel.src.dto.Navigator
import com.whitebear.travel.src.dto.Place

class NavPlaceAdapter() : RecyclerView.Adapter<NavPlaceAdapter.NavViewHolder>(){
    var list = mutableListOf<Navigator>()
    inner class NavViewHolder(private val binding:ItemPlaceNavBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(navi:Navigator){
            binding.navi = navi
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavViewHolder {
        return NavViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_place_nav,parent,false))

    }

    override fun onBindViewHolder(holder: NavViewHolder, position: Int) {
        holder.apply {
            bind(list[position])
            itemView.findViewById<ImageButton>(R.id.fragment_nav_delete).setOnClickListener {
                itemClickListener.onClick(it,position,list[position].idx)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    interface ItemClickListener {
        fun onClick(view: View, position: Int, placeId:Int)
    }

    private lateinit var itemClickListener : ItemClickListener

    fun setOnItemClickListenenr(itemClickListener : ItemClickListener){
        this.itemClickListener = itemClickListener
    }
}