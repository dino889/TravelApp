package com.whitebear.travel.src.main.location

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.whitebear.travel.R
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentLocationMapBinding
import com.whitebear.travel.src.main.MainActivity
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapPolyline
import net.daum.mf.map.api.MapView


class LocationMapFragment : BaseFragment<FragmentLocationMapBinding>(FragmentLocationMapBinding::bind, R.layout.fragment_location_map) {
    private val TAG = "LocationMapFragment"
    private lateinit var mainActivity : MainActivity

    private lateinit var mapView:MapView
    var markerArr = arrayListOf<MapPoint>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.hideBottomNav(true)

        initListener()
    }

    private fun initListener(){
        initKakaoMap()
        // 캠핑장 마커 on / off
    }

    private fun initKakaoMap(){
        mapView = MapView(requireContext())
        if(mapView.parent != null){
            (mapView.parent as ViewGroup).removeView(mapView)
        }
        val  mapViewContainer = binding.mapFragmentPlaceMapView as ViewGroup
        mapViewContainer.addView(mapView)
        addPing()
    }

    private fun addPing(){
        markerArr = arrayListOf()
        mainViewModel.placesByGps.observe(viewLifecycleOwner, {
            if(it != null && it.isNotEmpty()) {
                for(item in it){
                    val mapPoint = MapPoint.mapPointWithGeoCoord(item.lat, item.long)
                    markerArr.add(mapPoint)
                }
                val first = it[0]
                val mapPoint = MapPoint.mapPointWithGeoCoord(first.lat, first.long)
                mapView.setMapCenterPoint(mapPoint, true)
                mapView.setZoomLevel(6,true)
                setPing(markerArr)
            } else {
                showCustomToast("사용자 주변에 장소가 없습니다.")
            }
        })
    }

    private fun setPing(markerArr : ArrayList<MapPoint>) {
        removePing()
        val list = arrayListOf<MapPOIItem>()
        for(i in 0..markerArr.size-1){
            val marker = MapPOIItem()
            marker.itemName = mainViewModel.placesByGps.value!![i].name
            marker.mapPoint = markerArr[i]
            marker.markerType = MapPOIItem.MarkerType.BluePin
            list.add(marker)
        }
        mapView.addPOIItems(list.toArray(arrayOfNulls(list.size)))
    }

    private fun removePing(){
        mapView.removeAllPOIItems()
        mapView.removeAllPolylines()
    }

}