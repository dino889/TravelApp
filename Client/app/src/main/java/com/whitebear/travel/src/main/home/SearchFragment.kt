package com.whitebear.travel.src.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.whitebear.travel.R
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentSearchBinding

class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::bind,R.layout.fragment_search) {
    private lateinit var pagerAdapter: SearchTabPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListener()
    }
    private fun setListener(){
        initButtons()
        initTabLayout()
    }
    private fun initButtons(){
        binding.fragmentSearchAppBarBack.setOnClickListener {
            this@SearchFragment.findNavController().popBackStack()
        }
    }
    private fun initTabLayout(){
        pagerAdapter = SearchTabPagerAdapter(this)
        val tabList = arrayListOf("장소", "경로")

        pagerAdapter.addFragment(SearchPlaceFragment.newInstance())
        pagerAdapter.addFragment(SearchRouteFragment.newInstance())

        binding.fragmentSearchViewPager.adapter = pagerAdapter
        TabLayoutMediator(binding.fragmentSearchTabLayout, binding.fragmentSearchViewPager){ tab, position ->
            tab.text = tabList.get(position)
        }.attach()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}