package com.whitebear.travel.src.main.route

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
import com.whitebear.travel.databinding.ItemRouteBinding
import com.whitebear.travel.src.dto.Place
import com.whitebear.travel.src.dto.Route
import com.whitebear.travel.src.network.viewmodel.MainViewModel

class RouteAdapter(val mainViewModel: MainViewModel) : RecyclerView.Adapter<RouteAdapter.RouteViewHolder>(),Filterable{
    var list = mutableListOf<Route>()
    var likeList = mutableListOf<Route>()
    var filteredList = list
    inner class RouteViewHolder(private val binding : ItemRouteBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(route : Route){
            for(i in likeList){
                if(route.id == i.id){
                    binding.frragmentRouteRouteLike.progress = 0.5F
                    break
                }
                binding.frragmentRouteRouteLike.progress = 0F
            }
            binding.viewModel = mainViewModel
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
            var areaName = filteredList[position].name.substring(0,4)
            var heart = itemView.findViewById<LottieAnimationView>(R.id.frragment_route_routeLike)
            itemView.setOnClickListener {
                if(heart.progress > 0.3f){
                    itemClickListener.onClick(it,position,filteredList[position].id, true, areaName)
                }else{
                    itemClickListener.onClick(it,position,filteredList[position].id, false, areaName)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    interface ItemClickListener {
        fun onClick(view: View, position: Int, routeId:Int, heartFlag : Boolean, areaName:String)
    }
    private lateinit var itemClickListener : ItemClickListener
    fun setOnItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint.toString()
                filteredList = if(charString.isEmpty()){
                    list
                }else{
                    val filteringList = ArrayList<Route>()
                    for(item in list){
                        if(item.description.contains(charString)) filteringList.add(item)
                        if(item.name.contains(charString)) filteringList.add(item)
                    }
                    filteringList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as MutableList<Route>
                notifyDataSetChanged()
            }

        }
    }

}