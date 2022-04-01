package com.whitebear.travel.src.main.route

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.whitebear.travel.R
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentRouteBinding

class RouteFragment : BaseFragment<FragmentRouteBinding>(FragmentRouteBinding::bind,R.layout.fragment_route) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
       }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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