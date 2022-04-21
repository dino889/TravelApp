package com.whitebear.travel.src.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.whitebear.travel.R
import com.whitebear.travel.databinding.ItemBestPlaceBinding
import com.whitebear.travel.src.dto.Place

class BestPlaceAdapter : RecyclerView.Adapter<BestPlaceAdapter.BestViewHolder>() {
    var list = mutableListOf<Place>()
    var likelist = mutableListOf<Place>()
    inner class BestViewHolder(private val binding:ItemBestPlaceBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(place: Place){
            binding.place = place
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestViewHolder {
        return BestViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_best_place,parent,false))
    }

    override fun onBindViewHolder(holder: BestViewHolder, position: Int) {
        holder.apply {
            bind(list[position])
            for(i in 0..likelist.size-1){
                if(likelist[i].id == list[position].id){
                    itemView.setOnClickListener{
                        itemClickListener.onClick(it,position,list[position].id,true)
                    }
                }else{
                    itemView.setOnClickListener{
                        itemClickListener.onClick(it,position,list[position].id,false)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return 5
    }
    interface ItemClickListener {
        fun onClick(view: View, position: Int, placeId:Int, heartFlag:Boolean)
    }
    private lateinit var itemClickListener : ItemClickListener
    fun setOnItemClickListenenr(itemClickListener : ItemClickListener){
        this.itemClickListener = itemClickListener
    }
}