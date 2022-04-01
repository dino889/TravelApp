package com.whitebear.travel.src.main.my

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.whitebear.travel.R
import com.whitebear.travel.config.ApplicationClass
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentLikePlaceRouteBinding
import com.whitebear.travel.src.main.MainActivity
import kotlinx.coroutines.runBlocking

class LikePlaceRouteFragment : BaseFragment<FragmentLikePlaceRouteBinding>(FragmentLikePlaceRouteBinding::bind, R.layout.fragment_like_place_route) {
    private lateinit var mainActivity : MainActivity
    private lateinit var likePlaceRouteRecyclerviewAdapter: LikePlaceRouteRecyclerviewAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerviewAdapter()
    }

    private fun initRecyclerviewAdapter() {

        likePlaceRouteRecyclerviewAdapter = LikePlaceRouteRecyclerviewAdapter()
//        likePlaceRouteRecyclerviewAdapter.list = it
        likePlaceRouteRecyclerviewAdapter.setItemClickListener(object : LikePlaceRouteRecyclerviewAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int) {

            }
        })

        binding.myScheduleFragmentRv.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = likePlaceRouteRecyclerviewAdapter
            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

    }
}