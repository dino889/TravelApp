package com.whitebear.travel.src.main.route

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.whitebear.travel.R
import com.whitebear.travel.config.ApplicationClass
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentRouteBinding
import com.whitebear.travel.src.main.MainActivity
import kotlinx.coroutines.runBlocking

private const val TAG = "RouteFragment"
class RouteFragment : BaseFragment<FragmentRouteBinding>(FragmentRouteBinding::bind,R.layout.fragment_route) {
    private lateinit var mainActivity:MainActivity
    private lateinit var routeAdapter: RouteAdapter
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
            mainViewModel.getRoutes("대구")
            mainViewModel.getRoutesLikes(ApplicationClass.sharedPreferencesUtil.getUser().id)
        }
        setListener()
    }
    fun setListener(){
        initTabLayout()
        initAdapter()
    }
    private fun initTabLayout(){
        var areas = mainViewModel.areas.value!!
        for(item in 0..areas.size-1){
            binding.fragmentRouteTabLayout.addTab(binding.fragmentRouteTabLayout.newTab().setText(areas[item].name))
        }
        routeAdapter = RouteAdapter()
        binding.fragmentRouteTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if(tab != null){
                    areaName = tab?.text.toString()
                }
                runBlocking {
                    mainViewModel.getRoutes(tab?.text.toString())
                }
                initAdapter()
                routeAdapter.notifyDataSetChanged()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

    }
    private fun initAdapter(){
        routeAdapter = RouteAdapter()
        routeAdapter.list = mainViewModel.routes.value!!
        mainViewModel.routes.observe(viewLifecycleOwner, {
            Log.d(TAG, "initAdapter: $it")
            routeAdapter.list = it
        })
        mainViewModel.routesLikes.observe(viewLifecycleOwner, {
            routeAdapter.likeList = it
        })
        binding.fragmentRouteRv.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
            adapter = routeAdapter
            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RouteFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}