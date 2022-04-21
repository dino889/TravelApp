package com.whitebear.travel.src.main.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.Lottie
import com.airbnb.lottie.LottieAnimationView
import com.whitebear.travel.R
import com.whitebear.travel.databinding.ItemBestRoutesBinding
import com.whitebear.travel.src.dto.Route

class BestRoutesAdapter : RecyclerView.Adapter<BestRoutesAdapter.BestViewHolder>() {
    var list = mutableListOf<Route>()
    var likelist = mutableListOf<Route>()
    inner class BestViewHolder(private val binding:ItemBestRoutesBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(route:Route){
            binding.route = route
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestViewHolder {
        return BestViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_best_routes,parent,false))
    }

    override fun onBindViewHolder(holder: BestViewHolder, position: Int) {
        holder.apply {
            bind(list[position])
            var heart = false
            var areaName = list[position].name.substring(0,4)
            itemView.setOnClickListener {
                Log.d("TAG", "onBindViewHolder: CLick?")
                for(i in 0..likelist.size-1){
                    if(likelist[i].id == list[position].id){
                        Log.d("TAG", "onBindViewHolder: true")
                        heart = true
                    }
                }
                Log.d("TAG", "onBindViewHolder: $heart")

                itemClickListener.onClick(it,position,list[position].id,heart, areaName)
            }

        }
    }

    override fun getItemCount(): Int {
        return 5
    }
    interface ItemClickListener {
        fun onClick(view: View, position: Int, routeId:Int, heartFlag:Boolean, areaName:String)
    }
    private lateinit var itemClickListener : ItemClickListener
    fun setOnItemClickListenenr(itemClickListener : ItemClickListener){
        this.itemClickListener = itemClickListener
    }
}