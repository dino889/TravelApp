package com.whitebear.travel.src.main.location

import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.whitebear.travel.R
import com.whitebear.travel.databinding.ItemPlaceBinding
import com.whitebear.travel.databinding.ItemPlacesAroundBinding
import com.whitebear.travel.src.dto.Place
import android.text.method.ScrollingMovementMethod




class AroundPlaceAdapter : RecyclerView.Adapter<AroundPlaceAdapter.PlaceViewHolder>(){
    lateinit var userLoc : Location
    var list = mutableListOf<Place>()
    var likeList = mutableListOf<Place>()

    inner class PlaceViewHolder(private val binding: ItemPlacesAroundBinding) : RecyclerView.ViewHolder(binding.root){
        val heart = binding.aroundPlaceItemLottiePlaceLike
        fun bind(place:Place){

            for(i in likeList){
                if(place.id == i.id){
                    binding.aroundPlaceItemLottiePlaceLike.progress = 0.5F
                    break
                }
                binding.aroundPlaceItemLottiePlaceLike.progress = 0.0F
            }

            binding.aroundPlaceItemTvDistance.movementMethod = ScrollingMovementMethod()

            binding.place = place
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        return PlaceViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_places_around, parent,false))
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = list[position]
        holder.apply {
            bind(place)

            itemView.setOnClickListener {
                if(heart.progress > 0.3f){
                    itemClickListener.onClick(it, position, place.id, true)
                }else{
                    itemClickListener.onClick(it, position, place.id, false)
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface ItemClickListener {
        fun onClick(view: View, position: Int, placeId:Int, heartFlag : Boolean)
    }

    private lateinit var itemClickListener : ItemClickListener

    fun setOnItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }

}