package com.whitebear.travel.src.main.my

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.whitebear.travel.R
import com.whitebear.travel.databinding.ItemFaqBinding
import com.whitebear.travel.src.dto.FAQ

class FAQRecyclerviewAdapter : RecyclerView.Adapter<FAQRecyclerviewAdapter.FAQViewHolder>() {
    var faqList = mutableListOf<FAQ>()

    inner class FAQViewHolder(private val binding: ItemFaqBinding) : RecyclerView.ViewHolder(binding.root) {
        val faqDetail = binding.faqItemIvDetail
        val faqContent = binding.faqItemTvContent

        fun bind(faq: FAQ) {
            binding.faq = faq
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        return FAQViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_faq, parent, false))
    }

    override fun onBindViewHolder(holder:FAQViewHolder, position: Int) {
        val faq = faqList[position]
        holder.apply {
            bind(faq)
            faqDetail.setOnClickListener {
                itemClickListener.onClick(it, faqContent, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return faqList.size
    }

    interface ItemClickListener{
        fun onClick(view: View, contentView: TextView, position: Int)
    }

    private lateinit var itemClickListener : ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }
}