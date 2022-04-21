package com.whitebear.travel.src.main.location

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentLocationMapBinding
import com.whitebear.travel.src.main.MainActivity
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.whitebear.travel.R
import com.whitebear.travel.src.dto.Place
import kotlinx.coroutines.runBlocking
import net.daum.mf.map.api.*
import net.daum.mf.map.api.MapPoint.GeoCoordinate


class LocationMapFragment : BaseFragment<FragmentLocationMapBinding>(FragmentLocationMapBinding::bind, R.layout.fragment_location_map), MapView.CurrentLocationEventListener {
    private val TAG = "LocationMapFragment"
    private lateinit var mainActivity : MainActivity

    private lateinit var mapView: MapView
    private lateinit var mapViewContainer : ViewGroup
    private var currentMapPoint : MapPoint? = null

    private var currentLat: Double = 35.869326
    private var currentLng: Double = 128.595565

    private var markerArr = arrayListOf<MapPoint>()

    private var range = 20.0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.hideBottomNav(true)


        mainViewModel.userLoc.observe(viewLifecycleOwner, {
            if(it != null) {
                runBlocking {
                    mainViewModel.getPlacesByGps(it.latitude, it.longitude, range)
                    currentLat = it.latitude
                    currentLng = it.longitude

                    setCircleByRange(currentLat, currentLng)
                }
            }
        })

        initListener()
    }

    private fun initListener(){
        initKakaoMap()
        initSpinner()
        backBtnClickEvent()
        // 캠핑장 마커 on / off
    }

    private fun initSpinner(){
        val rangeList = arrayListOf("20km", "10km", "5km", "1km")
        val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line, rangeList)
        binding.mapFragmentSpinnerRange.adapter = adapter

        binding.mapFragmentSpinnerRange.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                removePing()
                when (position) {
                    0 -> {
                        range = 20.0
                        if(currentLat == 35.869326) {
                            showCustomToast("현재 위치를 받아올 수 없습니다.")
                        }
                        mainViewModel.userLoc.observe(viewLifecycleOwner, {
                            if(it != null) {
                                runBlocking {
                                    mainViewModel.getPlacesByGps(it.latitude, it.longitude, range)
                                    currentLat = it.latitude
                                    currentLng = it.longitude
                                    setCircleByRange(currentLat, currentLng)
                                }
                            }
                        })
//                        setCircleByRange(currentLat, currentLng)

                    }
                    1 -> {
                        range = 10.0
                        if(currentLat == 35.869326) {
                            showCustomToast("현재 위치를 받아올 수 없습니다.")
                        }
                        runBlocking {
                            mainViewModel.getPlacesByGps(currentLat, currentLng, range)
                        }
                        setCircleByRange(currentLat, currentLng)
                    }
                    2 -> {
                        range = 5.0
                        if(currentLat == 35.869326) {
                            showCustomToast("현재 위치를 받아올 수 없습니다.")
                        }
                        runBlocking {
                            mainViewModel.getPlacesByGps(currentLat, currentLng, range)
                        }
                        setCircleByRange(currentLat, currentLng)
                    }
                    3 -> {
                        range = 1.0
                        if(currentLat == 35.869326) {
                            showCustomToast("현재 위치를 받아올 수 없습니다.")
                        }
                        runBlocking {
                            mainViewModel.getPlacesByGps(currentLat, currentLng, range)
                        }
                        setCircleByRange(currentLat, currentLng)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    private fun initKakaoMap(){
        mapView = MapView(requireContext())
        if(mapView.parent != null){
            (mapView.parent as ViewGroup).removeView(mapView)
        }
        mapViewContainer = binding.mapFragmentPlaceMapView as ViewGroup
        mapViewContainer.addView(mapView)

        if (!mainActivity.checkLocationServicesStatus()) {
            mainActivity.showDialogForLocationServiceSetting()
        } else {
            mainActivity.checkRunTimePermission()
        }

        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading

        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(currentLat, currentLng), 6, true);
        setCircleByRange(currentLat, currentLng)

        mainViewModel.placesByGps.observe(viewLifecycleOwner) {
            if (it == null || it.isEmpty()) {
                Snackbar.make(requireView(), "현재 위치 주변에 관광지가 없습니다.", Snackbar.LENGTH_LONG).show()
            } else {
                addPing()
            }
        }
    }

    private fun addPing(){
        markerArr = arrayListOf()
        val placeList = mainViewModel.placesByGps.value

        if(placeList != null && placeList.isNotEmpty()) {
            for(item in placeList){
                val mapPoint = MapPoint.mapPointWithGeoCoord(item.lat, item.long)
                markerArr.add(mapPoint)
            }
            setPing(markerArr, placeList)
        } else {
            showCustomToast("사용자 주변에 장소가 없습니다.")
        }
    }

    private fun setPing(markerArr : ArrayList<MapPoint>, placeList: MutableList<Place>) {
        removePing()
        val list = arrayListOf<MapPOIItem>()
        for(i in 0 until markerArr.size){
            val marker = MapPOIItem()
            marker.itemName = placeList[i].name
            marker.mapPoint = markerArr[i]
            marker.markerType = MapPOIItem.MarkerType.BluePin
            list.add(marker)
        }
        mapView.addPOIItems(list.toArray(arrayOfNulls(list.size)))
    }

    private fun setCircleByRange(curLat: Double, curLng: Double) {
        val mapPoint = MapPoint.mapPointWithGeoCoord(curLat, curLng)
        val circle1 = MapCircle(
            mapPoint,  // center
            (range * 1000).toInt(),  // radius
            Color.TRANSPARENT,  // strokeColor
            Color.argb(100, 12, 49, 122) // fillColor
        )
        circle1.tag = 1234
        mapView.addCircle(circle1)

        val circle2 = MapCircle(
            MapPoint.mapPointWithGeoCoord(curLat, curLng),
            0,  // radius
            Color.argb(128, 255, 0, 0),  // strokeColor
            Color.argb(128, 255, 255, 0) // fillColor
        )
        circle2.tag = 5678
        mapView.addCircle(circle2)

        val mapPointBoundsArray = arrayOf(circle1.bound, circle2.bound)
        val mapPointBounds = MapPointBounds(mapPointBoundsArray)
        val padding = 50 // px

        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding))

    }

    private fun removePing() {
        mapView.removeAllPOIItems()
        mapView.removeAllCircles()
    }

    private fun backBtnClickEvent() {
        binding.mapFragmentIvBack.setOnClickListener {
            this@LocationMapFragment.findNavController().popBackStack()
        }
    }


//    단말의 현위치 좌표값을 통보받을 수 있다.
    override fun onCurrentLocationUpdate(p0: MapView, p1: MapPoint, p2: Float) {
//        removePing()
        val mapPointGeo: GeoCoordinate = p1.mapPointGeoCoord
        currentLat = mapPointGeo.latitude
        currentLng = mapPointGeo.longitude
//        runBlocking {
//            mainViewModel.getPlacesByGps(mapPointGeo.latitude, mapPointGeo.longitude, range)
//        }
        currentMapPoint = MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude)

        //이 좌표로 지도 중심 이동
        p0.setMapCenterPoint(currentMapPoint, true)
        p0.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(currentLat, currentLng), 6, true);

        setCircleByRange(currentLat, currentLng)
    }

//    단말의 방향(Heading) 각도값을 통보받을 수 있다.
//    MapView.setCurrentLocationTrackingMode 메소드를 통해
//    사용자 현위치 트래킹과 나침반 모드가 켜진 경우(CurrentLocationTrackingMode.TrackingModeOnWithHeading)
//    단말의 방향 각도값이 주기적으로 delegate 객체에 통보된다.
    override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {

    }

    // 현위치 갱신 작업에 실패한 경우 호출된다.
    override fun onCurrentLocationUpdateFailed(p0: MapView?) {
        Log.e(TAG, "onCurrentLocationUpdateFailed: ")
    }

    // 현위치 트랙킹 기능이 사용자에 의해 취소된 경우 호출된다.
    // 처음 현위치를 찾는 동안에 현위치를 찾는 중이라는 Alert Dialog 인터페이스가 사용자에게 노출된다.
    // 첫 현위치를 찾기전에 사용자가 취소 버튼을 누른 경우 호출 된다.
    override fun onCurrentLocationUpdateCancelled(p0: MapView?) {
        showCustomToast("현재 위치 트랙킹 기능을 취소하셨습니다.")
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
        mapViewContainer.removeAllViews()
        mainActivity.hideBottomNav(false)
    }
}