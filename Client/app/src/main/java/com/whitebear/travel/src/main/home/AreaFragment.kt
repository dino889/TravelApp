package com.whitebear.travel.src.main.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.whitebear.travel.R
import com.whitebear.travel.config.ApplicationClass
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentAreaBinding
import com.whitebear.travel.src.dto.Place
import com.whitebear.travel.src.main.place.PlaceAdapter
import kotlinx.coroutines.runBlocking

private const val TAG = "AreaFragment"
class AreaFragment : BaseFragment<FragmentAreaBinding>(FragmentAreaBinding::bind,R.layout.fragment_area) {
    var areaName = ""
    var areaId = 0

    private lateinit var placeAdapter : PlaceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            areaName = getString("areaName")!!
            areaId = getInt("areaId")

            Log.d(TAG, "onCreate: $areaName $areaId")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = mainViewModel
        runBlocking {
            mainViewModel.getAreaOne(areaId)
            mainViewModel.getPlaces(areaName)
            mainViewModel.getPlaceLikes(ApplicationClass.sharedPreferencesUtil.getUser().id)
        }
        setListener()
    }
    fun setListener(){
        initButtons()
        initAdapter()
    }
    private fun initButtons(){
        binding.fragmentAreaBack.setOnClickListener {
            this@AreaFragment.findNavController().popBackStack()
        }
    }
    private fun initAdapter(){
        placeAdapter = PlaceAdapter()
        placeAdapter.list = mainViewModel.places.value!!
        placeAdapter.filter.filter("")
        mainViewModel.places.observe(viewLifecycleOwner, {
            Log.d(TAG, "initAdapter: $it")
            placeAdapter.list = it
        })
        mainViewModel.placeLikes.observe(viewLifecycleOwner, {
            placeAdapter.likeList = it
        })

        binding.fragmentAreaRv.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
            adapter = placeAdapter
            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        placeAdapter.setOnItemClickListener(object : PlaceAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, placeId: Int, heartFlag: Boolean) {
                var place = bundleOf("placeId" to placeId, "heartFlag" to heartFlag)
                this@AreaFragment.findNavController().navigate(R.id.placeDetailFragment, place)
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AreaFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}