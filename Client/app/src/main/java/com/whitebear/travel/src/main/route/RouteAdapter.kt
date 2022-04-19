package com.whitebear.travel.src.main.route

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.whitebear.travel.R
import com.whitebear.travel.databinding.ItemRouteBinding
import com.whitebear.travel.src.dto.Route

class RouteAdapter : RecyclerView.Adapter<RouteAdapter.RouteViewHolder>(){
    var list = mutableListOf<Route>()
    var likeList = mutableListOf<Route>()
    var filteredList = list
    inner class RouteViewHolder(private val binding : ItemRouteBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(route : Route){
            Log.d("TAG", "bind: $route")
            for(i in likeList){
                if(route.id == i.id){
                    binding.frragmentRouteRouteLike.progress = 0.5F
                    break
                }
                binding.frragmentRouteRouteLike.progress = 0F
                //heart 안채우기
            }

            binding.route = route
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        return RouteViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_route,parent,false))
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        holder.apply {
            bind(filteredList[position])
            var heart = itemView.findViewById<LottieAnimationView>(R.id.frragment_route_routeLike)
            itemView.setOnClickListener {
                if(heart.progress > 0.3f){
                    itemClickListener.onClick(it,position,filteredList[position].id, true)
                }else{
                    itemClickListener.onClick(it,position,filteredList[position].id, false)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    interface ItemClickListener {
        fun onClick(view: View, position: Int, routeId:Int, heartFlag : Boolean)
    }
    private lateinit var itemClickListener : ItemClickListener
    fun setOnItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }

}