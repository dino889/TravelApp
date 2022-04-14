package com.whitebear.travel.src.main.place

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.appcompat.view.menu.MenuView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.whitebear.travel.R
import com.whitebear.travel.config.ApplicationClass
import com.whitebear.travel.databinding.ItemPlaceReviewBinding
import com.whitebear.travel.src.dto.Message
import com.whitebear.travel.src.dto.PlaceReview
import com.whitebear.travel.src.network.service.PlaceService
import kotlinx.coroutines.runBlocking
import retrofit2.Response

class PlaceReviewAdapter(context: Context) : RecyclerView.Adapter<PlaceReviewAdapter.ReviewViewHolder>() {
    var list = mutableListOf<PlaceReview>()
    inner class ReviewViewHolder(private val binding:ItemPlaceReviewBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(review:PlaceReview){
            binding.review = review
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        return ReviewViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_place_review,parent,false))
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.apply {
            bind(list[position])
            if(list[position].userId == ApplicationClass.sharedPreferencesUtil.getUser().id){
                itemView.findViewById<ImageButton>(R.id.fragment_placeReview_moreBtn).visibility = View.VISIBLE
            }
            itemView.findViewById<ImageButton>(R.id.fragment_placeReview_moreBtn).setOnClickListener {
                val popup: PopupMenu = PopupMenu(context,itemView.findViewById<ImageButton>(R.id.fragment_placeReview_moreBtn))
                MenuInflater(context).inflate(R.menu.popup_menu, popup.menu)

                popup.show()
                popup.setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.menu_delete ->{
                            itemClickListener.onClick(itemView,bindingAdapterPosition,list[bindingAdapterPosition].id)
                            return@setOnMenuItemClickListener true
                        }else->{
                        return@setOnMenuItemClickListener false
                    }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    interface ItemClickListener{
        fun onClick(view: View, position: Int, id: Int)
    }

    private lateinit var itemClickListener : ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }
}