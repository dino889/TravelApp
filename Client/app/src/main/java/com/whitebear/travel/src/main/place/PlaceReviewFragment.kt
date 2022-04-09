package com.whitebear.travel.src.main.place

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.whitebear.travel.R
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentPlaceReviewBinding

class PlaceReviewFragment : BaseFragment<FragmentPlaceReviewBinding>(FragmentPlaceReviewBinding::bind,R.layout.fragment_place_review) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {

        @JvmStatic
        fun newInstance(key:String,value:Int) =
            PlaceReviewFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}