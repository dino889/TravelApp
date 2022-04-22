package com.whitebear.travel.src.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.whitebear.travel.R
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentSearchPlaceBinding

class SearchPlaceFragment : BaseFragment<FragmentSearchPlaceBinding>(FragmentSearchPlaceBinding::bind,R.layout.fragment_search_place) {
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
        fun newInstance() =
            SearchPlaceFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}