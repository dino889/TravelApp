package com.whitebear.travel.src.main.location

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.whitebear.travel.R
import com.whitebear.travel.config.ApplicationClass
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentLocationBinding
import com.whitebear.travel.src.main.MainActivity
import kotlinx.coroutines.runBlocking

class LocationFragment : BaseFragment<FragmentLocationBinding>(FragmentLocationBinding::bind,R.layout.fragment_location) {
    private val TAG = "LocationFragment"
    private lateinit var mainActivity : MainActivity
    private lateinit var aroundPlaceAdapter: AroundPlaceAdapter

    private var currentLat: Double? = null
    private var currentLng: Double? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.userLoc.observe(viewLifecycleOwner, {
            if(it != null) {
                runBlocking {
                    mainViewModel.getPlacesByGps(it.latitude, it.longitude, 20.0)
                    mainViewModel.getPlaceLikes(ApplicationClass.sharedPreferencesUtil.getUser().id)
                    currentLat = it.latitude
                    currentLng = it.longitude
                }
            }
        })

        if(mainViewModel.userLoc.value == null) {
            binding.locationFragmentMyLoc.text = "현재 위치 정보를 받아올 수 없습니다."
        } else {
            val addr = mainActivity.getAddress(mainViewModel.userLoc.value!!).substring(5)
            binding.locationFragmentMyLoc.text = addr
            initRecyclerAdapter()
            initSpinner()
        }

        mapBtnClickEvent()

    }

    private fun initRecyclerAdapter() {

        aroundPlaceAdapter = AroundPlaceAdapter()
        mainViewModel.userLoc.observe(viewLifecycleOwner, {
            aroundPlaceAdapter.userLoc = it
        })

        mainViewModel.placeLikes.observe(viewLifecycleOwner) {
            aroundPlaceAdapter.likeList = it
        }

        mainViewModel.placesByGps.observe(viewLifecycleOwner) {
            aroundPlaceAdapter.list = it
        }

        binding.locationFragmentRv.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
            adapter = aroundPlaceAdapter
            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        aroundPlaceAdapter.setOnItemClickListener(object: AroundPlaceAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, placeId: Int, heartFlag : Boolean) {
                val place = bundleOf("placeId" to placeId, "heartFlag" to heartFlag)
                this@LocationFragment.findNavController().navigate(R.id.placeDetailFragment, place)
            }
        })
    }

    private fun mapBtnClickEvent() {
        binding.locationFragmentMap.setOnClickListener {
            this@LocationFragment.findNavController().navigate(R.id.action_locationFragment_to_mapFragment)
        }
    }

    private fun initSpinner() {
        val rangeList = arrayListOf("20km", "10km", "5km", "1km")
        val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line, rangeList)
        binding.locationFragmentSpinnerRange.adapter = adapter

        binding.locationFragmentSpinnerRange.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> {
                        mainViewModel.userLoc.observe(viewLifecycleOwner, {
                            if(it != null) {
                                runBlocking {
                                    mainViewModel.getPlacesByGps(it.latitude, it.longitude, 20.0)
                                    mainViewModel.getPlaceLikes(ApplicationClass.sharedPreferencesUtil.getUser().id)
                                }
                            }
                        })
                        aroundPlaceAdapter.notifyDataSetChanged()
                    }
                    1 -> {
                        if(currentLat == null) {
                            showCustomToast("현재 위치 정보를 받을 수 없습니다. ")
                        } else {
                            runBlocking {
                                mainViewModel.getPlacesByGps(currentLat!!, currentLng!!, 10.0)
                                mainViewModel.getPlaceLikes(ApplicationClass.sharedPreferencesUtil.getUser().id)
                            }
                            aroundPlaceAdapter.notifyDataSetChanged()
                        }
                    }
                    2 -> {
                        if(currentLat == null) {
                            showCustomToast("현재 위치 정보를 받을 수 없습니다. ")
                        } else {
                            runBlocking {
                                mainViewModel.getPlacesByGps(currentLat!!, currentLng!!, 5.0)
                                mainViewModel.getPlaceLikes(ApplicationClass.sharedPreferencesUtil.getUser().id)
                            }
                            aroundPlaceAdapter.notifyDataSetChanged()
                        }
                    }
                    3 -> {
                        if(currentLat == null) {
                            showCustomToast("현재 위치 정보를 받을 수 없습니다. ")
                        } else {
                            runBlocking {
                                mainViewModel.getPlacesByGps(currentLat!!, currentLng!!, 1.0)
                                mainViewModel.getPlaceLikes(ApplicationClass.sharedPreferencesUtil.getUser().id)
                            }
                            aroundPlaceAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

}