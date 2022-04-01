package com.whitebear.travel.src.main.place

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.whitebear.travel.R
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentPlaceDetailBinding

class PlaceDetailFragment : BaseFragment<FragmentPlaceDetailBinding>(FragmentPlaceDetailBinding::bind,R.layout.fragment_place_detail) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListener()
    }
    fun setListener(){
        initTabLayout()
    }
    fun initTabLayout(){
        val tabList = arrayListOf<String>("INFO","REVIEW")

        val pagerAdapter = PlaceDetailPagerAdapter(this)
        pagerAdapter.addFragment(PlaceInfoFragment.newInstance())
        pagerAdapter.addFragment(PlaceReviewFragment.newInstance())

        binding.fragmentPlaceDetailVp.adapter = pagerAdapter
        TabLayoutMediator(binding.fragmentPlaceDetailTabLayout, binding.fragmentPlaceDetailVp){ tab,position ->
            tab.text = tabList.get(position)
        }
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlaceDetailFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}