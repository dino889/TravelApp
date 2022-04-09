package com.whitebear.travel.src.main.place

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.whitebear.travel.R
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentPlaceInfoBinding
import kotlinx.coroutines.runBlocking

private const val TAG = "PlaceInfoFragment"
class PlaceInfoFragment : BaseFragment<FragmentPlaceInfoBinding>(FragmentPlaceInfoBinding::bind,R.layout.fragment_place_info) {
    var placeId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            placeId = it.getInt("placeId")
            Log.d(TAG, "onCreateInfo: $placeId")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = mainViewModel
        runBlocking {
            mainViewModel.getPlace(placeId)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(key:String, value:Int) =
            PlaceInfoFragment().apply {
                arguments = Bundle().apply {
                    putInt(key,value)
                }
            }
    }
}