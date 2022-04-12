package com.whitebear.travel.src.network.binding

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.whitebear.travel.src.dto.Area
import com.whitebear.travel.src.dto.Place
import com.whitebear.travel.src.dto.PlaceReview
import com.whitebear.travel.src.main.home.AreaAdapter
import com.whitebear.travel.src.main.place.PlaceAdapter
import com.whitebear.travel.src.main.place.PlaceReviewAdapter

@BindingAdapter("imageUrlArea")
fun bindImageArea(imgView:ImageView, imgUrl:String?){
    Glide.with(imgView.context)
        .load(imgUrl)
        .circleCrop()
        .into(imgView)
}
@BindingAdapter("areaListData")
fun bindAreaRecyclerView(recyclerView: RecyclerView, data:List<Area>?){
    var adapter = recyclerView.adapter as AreaAdapter
    if(recyclerView.adapter == null){
        adapter.setHasStableIds(true)
        recyclerView.adapter = adapter
    }else{
        adapter = recyclerView.adapter as AreaAdapter
    }

    adapter.list = data as MutableList<Area>
    adapter.notifyDataSetChanged()
}

@BindingAdapter("imageUrlPlace")
fun bindImagePlace(imgView:ImageView, imgUrl: String?){
    Glide.with(imgView.context)
        .load(imgUrl)
        .into(imgView)
}

@SuppressLint("SetTextI18n")
@BindingAdapter("textViewContent")
fun bindTextViewContent(textView: TextView, content:String){
    if(content.length > 35){
        textView.text = content.substring(0,35)+"..."
    }else{
        textView.text = content
    }

}
@BindingAdapter("textViewTitle")
fun bindTextViewTitle(textView: TextView, title:String){
    if(title.length>10){
        textView.text = title.substring(0,9)+".."
    }else{
        textView.text = title
    }
}
@BindingAdapter("placeListData")
fun bindPlaceRecyclerView(recyclerView: RecyclerView, data:List<Place>?){
    var adapter = recyclerView.adapter as PlaceAdapter
    if(recyclerView.adapter == null){
        adapter.setHasStableIds(true)
        recyclerView.adapter = adapter
    }else{
        adapter = recyclerView.adapter as PlaceAdapter
    }
    adapter.list = data as MutableList<Place>
    adapter.notifyDataSetChanged()
}
@BindingAdapter("placeReviewListData")
fun bindPlaceReviewRecyclerView(recyclerView: RecyclerView, data:List<PlaceReview>?) {
    var adapter = recyclerView.adapter as PlaceReviewAdapter
    if(recyclerView.adapter == null){
        adapter.setHasStableIds(true)
        recyclerView.adapter = adapter
    }else{
        adapter = recyclerView.adapter as PlaceReviewAdapter
    }

    adapter.list = data as MutableList<PlaceReview>
    adapter.notifyDataSetChanged()
}
@BindingAdapter("textViewTotalReview")
fun bindTextViweReviewTotal(textView: TextView, size:Int){
    textView.text = "총 ${size}건"
}