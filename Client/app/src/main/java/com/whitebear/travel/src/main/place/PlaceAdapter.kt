package com.whitebear.travel.src.main.place

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
import com.whitebear.travel.databinding.ItemPlaceBinding
import com.whitebear.travel.src.dto.Place

class PlaceAdapter : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() ,Filterable{
    var list = mutableListOf<Place>()
    var likeList = mutableListOf<Place>()
    var filteredList = list
    inner class PlaceViewHolder(private val binding: ItemPlaceBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(place:Place){

            for(i in likeList){
                if(place.id == i.id){
                    Log.d("TAG", "bind: ${place.id}")
                    binding.frragmentPlacePlaceLike.progress = 0.5F
                    break
                }
                binding.frragmentPlacePlaceLike.progress = 0.0F
            }
            binding.place = place
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        return PlaceViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_place,parent,false))
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.apply {
            bind(filteredList[position])
            var heart = itemView.findViewById<LottieAnimationView>(R.id.frragment_place_placeLike)
            itemView.setOnClickListener {
                if( heart.progress > 0.3f){
                    itemClickListener.onClick(it, position, filteredList[position].id, true)
                }else{
                    itemClickListener.onClick(it, position, filteredList[position].id, false)

                }

            }
        }
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }
    interface ItemClickListener {
        fun onClick(view: View, position: Int, placeId:Int, heartFlag : Boolean)
    }
    private lateinit var itemClickListener : ItemClickListener
    fun setOnItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint.toString()
                filteredList = if(charString.isEmpty()){
                    list
                }else{
                    val filteringList = ArrayList<Place>()
                    for( item in list ){

                        if(item.address.contains(charString)) filteringList.add(item)
                        if(item.description.contains(charString)) filteringList.add(item)
                        if(item.name.contains(charString)) filteringList.add(item)
                        if(item.summary.contains(charString)) filteringList.add(item)

                        Log.d("TAG", "performFiltering: $filteringList")
                    }
                    filteringList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as MutableList<Place>
                notifyDataSetChanged()
            }

        }
    }

}