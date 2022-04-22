package com.whitebear.travel.src.main.place

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.whitebear.travel.R
import com.whitebear.travel.config.ApplicationClass
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentPlaceBinding
import com.whitebear.travel.src.dto.Keyword
import com.whitebear.travel.src.main.MainActivity
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.time.LocalDateTime

private const val TAG = "PlaceFragment"
class PlaceFragment : BaseFragment<FragmentPlaceBinding>(FragmentPlaceBinding::bind, R.layout.fragment_place) {
    private lateinit var mainActivity : MainActivity
    private lateinit var placeAdapter: PlaceAdapter

    private var areaName = "대구"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = mainViewModel
        runBlocking {
            mainViewModel.getPlaces("대구")
            mainViewModel.getPlaceLikes(ApplicationClass.sharedPreferencesUtil.getUser().id)
        }
        setListener()
    }
    fun setListener(){
        initAdapter()
        initSpinner()
        initTabLayout()
    }

    fun initSpinner(){
        var spinnerArr = arrayListOf<String>("별점순","리뷰 적은순","리뷰 많은순")
        val adapter = ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, spinnerArr)
        binding.fragmentPlaceFilterSpinner.adapter = adapter

        binding.fragmentPlaceFilterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(position == 0){
                    Log.d(TAG, "onItemSelected: $areaName")
                    runBlocking {
                        mainViewModel.getPlaces(areaName)
                    }
                }
                if(position == 1){
                    runBlocking {
                        Log.d(TAG, "onItemSelected: $areaName")
                        mainViewModel.getPlacesToSort(areaName,"review")
                    }
                    Log.d(TAG, "onItemSelected: 1")
                }
                if(position == 2){
                    runBlocking {
                        mainViewModel.getPlacesToSort(areaName,"review_asc")
                    }
                    Log.d(TAG, "onItemSelected: 2")
                }

                initAdapter()
                placeAdapter.notifyDataSetChanged()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

    }
    fun initAdapter(){

        placeAdapter = PlaceAdapter()

        placeAdapter.list = mainViewModel.places.value!!
        mainViewModel.placeLikes.observe(viewLifecycleOwner) {
            placeAdapter.likeList = it
        }
        mainViewModel.places.observe(viewLifecycleOwner) {
            Log.d(TAG, "initAdapter: $it")
            placeAdapter.list = it
        }
        binding.fragmentPlaceRv.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
            adapter = placeAdapter
            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        placeAdapter.setOnItemClickListener(object: PlaceAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, placeId: Int, heartFlag : Boolean) {
                Log.d(TAG, "onClick: $heartFlag")
                var place = bundleOf("placeId" to placeId, "heartFlag" to heartFlag)

                this@PlaceFragment.findNavController().navigate(R.id.placeDetailFragment, place)
            }
        })
    }
    fun initTabLayout(){
        var areas = mainViewModel.areas.value!!
        for(item in 0..areas.size-1){
            binding.fragmentPlaceTabLayout.addTab(binding.fragmentPlaceTabLayout.newTab().setText(areas[item].name))
        }
        placeAdapter = PlaceAdapter()
        binding.fragmentPlaceTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.fragmentPlaceFilterSpinner.setSelection(0)
                if (tab != null) {
                    areaName = tab?.text.toString()
                }
                runBlocking {
                    mainViewModel.getPlaces(tab?.text.toString())
                }
                initAdapter()
                placeAdapter.notifyDataSetChanged()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

    }
    companion object {
        fun newInstance(param1: String, param2: String) =
            PlaceFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}