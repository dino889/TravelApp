package com.whitebear.travel.src.main.home

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.whitebear.travel.R
import com.whitebear.travel.config.ApplicationClass
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentSearchPlaceBinding
import com.whitebear.travel.src.dto.Keyword
import com.whitebear.travel.src.main.place.PlaceAdapter
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat

class SearchPlaceFragment : BaseFragment<FragmentSearchPlaceBinding>(FragmentSearchPlaceBinding::bind,R.layout.fragment_search_place) {
    private lateinit var placeAdapter : PlaceAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = mainViewModel
        runBlocking {
            mainViewModel.getPlaces("")
            mainViewModel.getPlaceLikes(ApplicationClass.sharedPreferencesUtil.getUser().id)
        }
        initAdapter()
        initSearch()
    }
    private fun initSearch(){
        placeAdapter = PlaceAdapter()
//        placeAdapter.filter.filter("")
        binding.fragmentSearchPlaceSv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                var curTime = System.currentTimeMillis()
                var formatter = SimpleDateFormat("yyyy-MM-dd HH:ss")
                var nows = formatter.format(curTime)
                if(query!=null){
                    var keywords = Keyword(
                        query,
                        "장소",
                        nows
                    )
                    mainViewModel.insertKeywords(keywords)

                    return false
                }
                return true
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
    private fun initAdapter(){
        placeAdapter = PlaceAdapter()
        mainViewModel.places.observe(viewLifecycleOwner){
            placeAdapter.list = it
        }
        mainViewModel.placeLikes.observe(viewLifecycleOwner){
            placeAdapter.likeList = it
        }
        binding.fragmentSearchPlaceRv.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter = placeAdapter
        }
        placeAdapter.setOnItemClickListener(object : PlaceAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, placeId: Int, heartFlag: Boolean) {
                var place = bundleOf("placeId" to placeId, "heartFlag" to heartFlag)
                this@SearchPlaceFragment.findNavController().navigate(R.id.placeDetailFragment,place)
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SearchPlaceFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}