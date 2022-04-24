package com.whitebear.travel.src.main.home

import android.content.Context
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
import com.google.android.material.tabs.TabLayout
import com.whitebear.travel.R
import com.whitebear.travel.config.ApplicationClass
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentAreaBinding
import com.whitebear.travel.src.dto.Place
import com.whitebear.travel.src.main.MainActivity
import com.whitebear.travel.src.main.place.PlaceAdapter
import kotlinx.coroutines.runBlocking

private const val TAG = "AreaFragment"
class AreaFragment : BaseFragment<FragmentAreaBinding>(FragmentAreaBinding::bind,R.layout.fragment_area) {
    var areaName = ""
    var areaId = 0

    private lateinit var placeAdapter : PlaceTypeAdapter
    private lateinit var mainActivity:MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            areaName = getString("areaName")!!
            areaId = getInt("areaId")
            Log.d(TAG, "onCreate: $areaName $areaId")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = mainViewModel

        if(areaName.length < 4){
            areaName = areaName.toString().substring(0,2)
        }
        runBlocking {
            mainViewModel.getCategorys()
            mainViewModel.getAreaOne(areaId)
            mainViewModel.getPlaces(areaName)
            mainViewModel.getPlaceLikes(ApplicationClass.sharedPreferencesUtil.getUser().id)
        }
        mainActivity.hideBottomNav(true)
        setListener()
    }
    fun setListener(){
        initButtons()
        initTabLayout()
        initAdapter()
    }
    private fun initButtons(){
        binding.fragmentAreaBack.setOnClickListener {
            this@AreaFragment.findNavController().popBackStack()
        }
    }
    private fun initAdapter(){
        placeAdapter = PlaceTypeAdapter()
        placeAdapter.rv = binding.fragmentAreaRv
        placeAdapter.tv = binding.areaFragmentTvVisible

        placeAdapter.list = mainViewModel.places.value!!
        placeAdapter.filter.filter("")
        mainViewModel.places.observe(viewLifecycleOwner) {
            placeAdapter.list = it
        }
        mainViewModel.placeLikes.observe(viewLifecycleOwner) {
            placeAdapter.likeList = it
        }

        binding.fragmentAreaRv.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
            adapter = placeAdapter
            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        placeAdapter.setOnItemClickListener(object : PlaceTypeAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, placeId: Int, heartFlag: Boolean) {
                val place = bundleOf("placeId" to placeId, "heartFlag" to heartFlag)
                this@AreaFragment.findNavController().navigate(R.id.placeDetailFragment, place)
            }
        })
    }
    private fun initTabLayout(){
        val categorys = mainViewModel.categorys.value!!
        for(item in 0..categorys.size-1){
            binding.fragmentAreaCatetabLayout.addTab(binding.fragmentAreaCatetabLayout.newTab().setText(categorys[item]))
        }
        placeAdapter = PlaceTypeAdapter()
        binding.fragmentAreaCatetabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if(tab?.position == 0){
                    placeAdapter.filter.filter("")
                }
                if(tab?.position!! > 0){
                    placeAdapter.filter.filter(tab?.text.toString())

                }

                placeAdapter.notifyDataSetChanged()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }

}