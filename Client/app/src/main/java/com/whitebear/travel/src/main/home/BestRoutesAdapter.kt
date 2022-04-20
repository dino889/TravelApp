package com.whitebear.travel.src.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.whitebear.travel.R
import com.whitebear.travel.databinding.ItemBestRoutesBinding
import com.whitebear.travel.src.dto.Route

class BestRoutesAdapter : RecyclerView.Adapter<BestRoutesAdapter.BestViewHolder>() {
    var list = mutableListOf<Route>()
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
        }
    }

    override fun getItemCount(): Int {
        return 5
    }
}