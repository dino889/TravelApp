package com.whitebear.travel.src.main.my

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.whitebear.travel.R
import com.whitebear.travel.databinding.ItemRecentlyKeywordBinding
import com.whitebear.travel.src.dto.Keyword

class RecentlyKeywordAdapter(): RecyclerView.Adapter<RecentlyKeywordAdapter.MyPostViewHolder>() {
    var list = mutableListOf<Keyword>()

    inner class MyPostViewHolder(private val binding: ItemRecentlyKeywordBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(keyword: Keyword) {
            binding.keyword = keyword
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): RecentlyKeywordAdapter.MyPostViewHolder {
        return MyPostViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_recently_keyword, parent, false))
    }

    override fun onBindViewHolder(holder: RecentlyKeywordAdapter.MyPostViewHolder, position: Int) {
        holder.apply {
            bind(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface ItemClickListener{
        fun onClick(view: View, position: Int)
    }

    private lateinit var itemClickListener : ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }
}