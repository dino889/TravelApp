package com.whitebear.travel.src.main.place

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.contains
import com.whitebear.travel.R
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentPlaceInfoBinding
import kotlinx.coroutines.runBlocking
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.lang.RuntimeException

private const val TAG = "PlaceInfoFragment"
class PlaceInfoFragment : BaseFragment<FragmentPlaceInfoBinding>(FragmentPlaceInfoBinding::bind,R.layout.fragment_place_info) {
    var placeId = 0
    private lateinit var mapView:MapView
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
        setListener()
    }
    fun setListener(){
        initKakaoMap()
    }
    private fun initKakaoMap(){
        mapView = MapView(requireContext())
        if(mapView.parent != null){
            (mapView.parent as ViewGroup).removeView(mapView)
        }

        var mapViewContainer = binding.fragmentPlaceInfoPlaceMapView as ViewGroup
        mapViewContainer.addView(mapView)
        var curLoc = mainViewModel.place.value!!
        Log.d(TAG, "initKakaoMap: ${curLoc.lat} // ${curLoc.long}")
        var mapPoint = MapPoint.mapPointWithGeoCoord(curLoc.lat, curLoc.long)
        mapView.setMapCenterPoint(mapPoint, true)
        mapView.setZoomLevel(4,true)
        var marker = MapPOIItem()
        marker.itemName = curLoc.name
        marker.mapPoint = mapPoint
        marker.markerType = MapPOIItem.MarkerType.RedPin
        mapView.addPOIItem(marker)
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

    override fun onStart() {
        super.onStart()
    }
    override fun onResume() {
        super.onResume()
        if(binding.fragmentPlaceInfoPlaceMapView.contains(mapView)!!){
            try{
                initKakaoMap()
            }catch (e:RuntimeException){
                Log.d(TAG, "onResume: ${e.printStackTrace()}")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.fragmentPlaceInfoPlaceMapView.removeView(mapView)
    }

    override fun onStop() {
        super.onStop()
        mapView.onSurfaceDestroyed()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onSurfaceDestroyed()
    }

}