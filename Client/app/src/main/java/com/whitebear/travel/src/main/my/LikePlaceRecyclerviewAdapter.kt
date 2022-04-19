package com.whitebear.travel.src.main.my

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.whitebear.travel.R
import com.whitebear.travel.databinding.ItemLikePlaceBinding
import com.whitebear.travel.src.dto.Place

class LikePlaceRecyclerviewAdapter() : RecyclerView.Adapter<LikePlaceRecyclerviewAdapter.MyScheduleViewHolder>() {
    var list = mutableListOf<Place>()

    inner class MyScheduleViewHolder(private val binding: ItemLikePlaceBinding) : RecyclerView.ViewHolder(binding.root) {
        val likePlace = binding.likePlaceRouteItemCv
        fun bind(place: Place) {
            binding.place = place
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyScheduleViewHolder {
        return MyScheduleViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_like_place, parent, false))
    }

    override fun onBindViewHolder(holder: MyScheduleViewHolder, position: Int) {
        val place = list[position]
        holder.apply {
            bind(place)
            likePlace.setOnClickListener {
                itemClickListener.onClick(it, position, place.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface ItemClickListener{
        fun onClick(view: View, position: Int, placeId: Int)
    }

    private lateinit var itemClickListener : ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }
}