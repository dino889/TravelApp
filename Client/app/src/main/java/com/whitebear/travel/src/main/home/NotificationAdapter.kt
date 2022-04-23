package com.whitebear.travel.src.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.whitebear.travel.R
import com.whitebear.travel.databinding.ItemNotificationBinding
import com.whitebear.travel.src.dto.Notification

class NotificationAdapter() : RecyclerView.Adapter<NotificationAdapter.NotiViewHolder>() {
    var list = mutableListOf<Notification>()

    inner class NotiViewHolder(private var binding: ItemNotificationBinding):RecyclerView.ViewHolder(binding.root){
        val delete = binding.notiItemTvDelete

        fun bind(noti:Notification){

            binding.noti = noti
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotiViewHolder {
        return NotiViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_notification,parent,false))
    }

    override fun onBindViewHolder(holder: NotiViewHolder, position: Int) {
        val notification = list[position]
        holder.apply {
            bind(notification)

            delete.setOnClickListener {
                itemClickListener.onClick(it, layoutPosition, notification.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun removeData(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    interface ItemClickListener {
        fun onClick(view: View, position: Int, id: Int)
    }

    private lateinit var itemClickListener : ItemClickListener

    fun setOnItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }
}