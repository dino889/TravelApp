package com.whitebear.travel.src.main.my

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.whitebear.travel.R
import com.whitebear.travel.databinding.ItemFaqCategoryBinding

class FAQCategoryRecyclerviewAdapter : RecyclerView.Adapter<FAQCategoryRecyclerviewAdapter.FAQCategoryViewHolder>() {
    var categoryList = listOf<String>()

    inner class FAQCategoryViewHolder(private val binding: ItemFaqCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        val faqCategory = binding.faqCategoryItemCv

        fun bind(category: String) {
            binding.faqCategoryItemTvCategoryName.text = category
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQCategoryViewHolder {
        return FAQCategoryViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_faq_category, parent, false))
    }

    override fun onBindViewHolder(holder: FAQCategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.apply {
            bind(category)
            faqCategory.setOnClickListener {
                itemClickListener.onClick(it, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    interface ItemClickListener{
        fun onClick(view: View, position: Int)
    }

    private lateinit var itemClickListener : ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }
}