package com.whitebear.travel.src.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.whitebear.travel.databinding.ItemBannerBinding

class BannerAdapter(var list:MutableList<Int>) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {
    inner class BannerViewHolder(private val binding:ItemBannerBinding) : RecyclerView.ViewHolder(binding.root){
        fun onBind(res:Int){
            binding.fragmentHomeBannerItem.setImageResource(res)
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BannerAdapter.BannerViewHolder {
        return BannerViewHolder(ItemBannerBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: BannerAdapter.BannerViewHolder, position: Int) {
        holder.apply {
            onBind(list[position % list.size])

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}