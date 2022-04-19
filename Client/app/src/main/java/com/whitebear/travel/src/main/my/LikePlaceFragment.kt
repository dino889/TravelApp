package com.whitebear.travel.src.main.my

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.whitebear.travel.R
import com.whitebear.travel.config.ApplicationClass
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentLikePlaceBinding
import com.whitebear.travel.src.main.MainActivity
import kotlinx.coroutines.runBlocking

class LikePlaceFragment : BaseFragment<FragmentLikePlaceBinding>(FragmentLikePlaceBinding::bind, R.layout.fragment_like_place) {
    private lateinit var mainActivity : MainActivity
    private lateinit var likePlaceRecyclerviewAdapter: LikePlaceRecyclerviewAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runBlocking {
            mainViewModel.getPlaceLikes(ApplicationClass.sharedPreferencesUtil.getUser().id)
        }

        initRecyclerviewAdapter()
    }

    private fun initRecyclerviewAdapter() {

        likePlaceRecyclerviewAdapter = LikePlaceRecyclerviewAdapter()
        mainViewModel.placeLikes.observe(viewLifecycleOwner, {
            likePlaceRecyclerviewAdapter.list = it
        })
        likePlaceRecyclerviewAdapter.setItemClickListener(object : LikePlaceRecyclerviewAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, placeId: Int) {
                this@LikePlaceFragment.findNavController().navigate(R.id.placeDetailFragment, bundleOf("placeId" to placeId))
            }
        })

        binding.myScheduleFragmentRv.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = likePlaceRecyclerviewAdapter
            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

    }
}