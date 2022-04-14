package com.whitebear.travel.src.main.place

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.whitebear.travel.R
import com.whitebear.travel.config.ApplicationClass
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentPlaceBinding
import com.whitebear.travel.src.main.MainActivity
import kotlinx.coroutines.runBlocking

private const val TAG = "PlaceFragment"
class PlaceFragment : BaseFragment<FragmentPlaceBinding>(FragmentPlaceBinding::bind, R.layout.fragment_place) {
    private lateinit var mainActivity : MainActivity
    private lateinit var placeAdapter: PlaceAdapter
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
        initButtonClick()
        initAdapter()
        initSpinner()
        initTabLayout()
    }

    fun initSpinner(){

    }
    fun initAdapter(){

        placeAdapter = PlaceAdapter()

        placeAdapter.list = mainViewModel.places.value!!
        mainViewModel.placeLikes.observe(viewLifecycleOwner, {
            placeAdapter.likeList = it
        })
        mainViewModel.places.observe(viewLifecycleOwner, {
            Log.d(TAG, "initAdapter: $it")
            placeAdapter.list = it
        })
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
        placeAdapter.filter.filter("")
        binding.fragmentPlaceSv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(TextUtils.isEmpty(newText)){
                    placeAdapter.filter.filter("")
                }else{
                    placeAdapter.filter.filter(newText.toString())
                    placeAdapter.notifyDataSetChanged()
                }

                return false
            }

        })
    }
    fun initButtonClick(){
        binding.fragmentPlaceAppBarBack.setOnClickListener {
            this@PlaceFragment.findNavController().popBackStack()
        }
    }
    fun initTabLayout(){
        var areas = mainViewModel.areas.value!!
        for(item in 0..areas.size-1){
            binding.fragmentPlaceTabLayout.addTab(binding.fragmentPlaceTabLayout.newTab().setText(areas[item].name))
        }
        placeAdapter = PlaceAdapter()
        binding.fragmentPlaceTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

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