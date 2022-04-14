package com.whitebear.travel.src.main.place

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
import com.whitebear.travel.src.dto.Place

class PlaceAdapter : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>(){
    var list = mutableListOf<Place>()
    var likeList = mutableListOf<Place>()
    var filteredList = list
    inner class PlaceViewHolder(private val binding: ItemPlaceBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(place:Place){

            for(i in likeList){
                if(place.id == i.id){
                    Log.d("TAG", "bind: ${place.id}")
                    binding.frragmentPlacePlaceLike.progress = 0.5F
                    break
                }
                binding.frragmentPlacePlaceLike.progress = 0.0F
            }
            binding.place = place
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        return PlaceViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_place,parent,false))
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.apply {
            bind(list[position])
            var heart = itemView.findViewById<LottieAnimationView>(R.id.frragment_place_placeLike)
            var heartFlag = false
            heartFlag = heart.progress > 0.3f
            itemView.setOnClickListener {
                if( heart.progress > 0.3f){
                    itemClickListener.onClick(it, position, list[position].id, true)
                }else{
                    itemClickListener.onClick(it, position, list[position].id, false)

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