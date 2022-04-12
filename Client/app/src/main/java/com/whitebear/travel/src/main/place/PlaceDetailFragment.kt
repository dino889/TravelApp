package com.whitebear.travel.src.main.place

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.whitebear.travel.R
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentPlaceDetailBinding
import kotlinx.coroutines.runBlocking

private const val TAG = "PlaceDetailFragment"
class PlaceDetailFragment : BaseFragment<FragmentPlaceDetailBinding>(FragmentPlaceDetailBinding::bind,R.layout.fragment_place_detail) {
    private var placeId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            placeId = getInt("placeId")
            Log.d(TAG, "onCreate: $placeId")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = mainViewModel
        runBlocking {
            mainViewModel.getPlace(placeId)
        }
        setListener()
    }
    fun setListener(){
        initTabLayout()
        initButtons()
    }
    fun initButtons(){
        binding.fragmentPlaceDetailAppBarBack.setOnClickListener {
            this@PlaceDetailFragment.findNavController().popBackStack()
        }
    }
    fun initTabLayout(){
        val tabList = arrayListOf<String>("INFO","REVIEW")

        val pagerAdapter = PlaceDetailPagerAdapter(this)
        pagerAdapter.addFragment(PlaceInfoFragment.newInstance("placeId",placeId))
        pagerAdapter.addFragment(PlaceReviewFragment.newInstance("placeId",placeId))

        binding.fragmentPlaceDetailVp.adapter = pagerAdapter
        TabLayoutMediator(binding.fragmentPlaceDetailTabLayout, binding.fragmentPlaceDetailVp){ tab,position ->
            tab.text = tabList.get(position)
        }.attach()
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