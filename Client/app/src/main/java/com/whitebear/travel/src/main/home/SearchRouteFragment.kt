package com.whitebear.travel.src.main.home

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
import com.whitebear.travel.R
import com.whitebear.travel.config.ApplicationClass
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentSearchRouteBinding
import com.whitebear.travel.src.dto.Keyword
import com.whitebear.travel.src.main.route.RouteAdapter
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat

class SearchRouteFragment : BaseFragment<FragmentSearchRouteBinding>(FragmentSearchRouteBinding::bind,R.layout.fragment_search_route) {
    private lateinit var routeAdapter : RouteAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        runBlocking {
            mainViewModel.getRoutes("")
            mainViewModel.getRoutesLikes(ApplicationClass.sharedPreferencesUtil.getUser().id)
        }
        initAdapter()
    }
    private fun initAdapter(){
        routeAdapter = RouteAdapter(mainViewModel)
        routeAdapter.list = mainViewModel.routes.value!!

        mainViewModel.routesLikes.observe(viewLifecycleOwner){
            routeAdapter.likeList = it
        }
        mainViewModel.routes.observe(viewLifecycleOwner){
            routeAdapter.list = it
        }
        binding.fragmentSearchRouteRv.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
            adapter = routeAdapter
            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        routeAdapter.setOnItemClickListener(object: RouteAdapter.ItemClickListener{
            override fun onClick(view: View, position: Int, routeId: Int, heartFlag: Boolean, areaName:String) {
                var route = bundleOf("routeId" to routeId, "heartFlag" to heartFlag, "areaName" to areaName)
                this@SearchRouteFragment.findNavController().navigate(R.id.routeFragment, route)
            }
        })
        binding.fragmentSearchRouteSv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                var curTime = System.currentTimeMillis()
                var formatter = SimpleDateFormat("yyyy-MM-dd HH:ss")
                var nows = formatter.format(curTime)
                if(query!=null){
                    var keywords = Keyword(
                        query,
                        "경로",
                        nows
                    )
                    mainViewModel.insertKeywords(keywords)
                    return false
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(TextUtils.isEmpty(newText)){
                    routeAdapter.filter.filter("")
                }else{
                    routeAdapter.filter.filter(newText.toString())
                    routeAdapter.notifyDataSetChanged()
                }
                return false
            }

        })
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SearchRouteFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}