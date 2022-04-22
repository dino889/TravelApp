package com.whitebear.travel.src.main.location

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.ContextCompat.startForegroundService
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.src.main.MainActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.whitebear.travel.R
import com.whitebear.travel.config.ApplicationClass
import com.whitebear.travel.databinding.FragmentLocationMapBinding
import com.whitebear.travel.src.dto.Place
import com.whitebear.travel.src.dto.Responses
import com.whitebear.travel.src.dto.RouteLike
import com.whitebear.travel.src.dto.camping.Camping
import com.whitebear.travel.src.main.route.RouteDetailAdapter
import com.whitebear.travel.src.network.service.DataService
import com.whitebear.travel.src.network.viewmodel.MainViewModel
import kotlinx.coroutines.runBlocking
import net.daum.mf.map.api.*
import net.daum.mf.map.api.MapPoint.GeoCoordinate
import retrofit2.Response

/**
 * @since 04/22/22
 * @author Jiwoo Choi
 */
class LocationMapFragment : BaseFragment<FragmentLocationMapBinding>(FragmentLocationMapBinding::bind, R.layout.fragment_location_map), MapView.CurrentLocationEventListener {
    private val TAG = "LocationMapFragment"
    private lateinit var mainActivity : MainActivity

    private lateinit var mapView: MapView
    private lateinit var mapViewContainer : ViewGroup
    private var currentMapPoint : MapPoint? = null

    private var currentLat: Double = 35.869326
    private var currentLng: Double = 128.595565

    private var markerArr = arrayListOf<MapPoint>()

    private var range = 50.0

    private var campingPOIList = arrayListOf<MapPOIItem>()
    private var visible = true // 캠핑장 정보 on / off
    private lateinit var eventListener : MarkerEventListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        eventListener = MarkerEventListener(requireContext(), mainViewModel)   // 마커 클릭 이벤트 리스너
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.hideBottomNav(true)


        mainViewModel.userLoc.observe(viewLifecycleOwner, {
            if(it != null) {
                runBlocking {
                    mainViewModel.getPlacesByGps(it.latitude, it.longitude, range)
//                    mainViewModel.getCampingList(it.latitude, it.longitude, (range * 1000).toInt())
                    currentLat = it.latitude
                    currentLng = it.longitude

                    setCircleByRange(currentLat, currentLng)
                }
            }
        })

//        getCampingList()

        initListener()
    }

    private fun initListener(){
        initKakaoMap()
        initSpinner()
        backBtnClickEvent()
        // 캠핑장 마커 on / off
        floatingBtnClickEvent()
    }

    private fun initSpinner(){
        val rangeList = arrayListOf("50km", "30km", "20km", "10km")
        val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line, rangeList)
        binding.mapFragmentSpinnerRange.adapter = adapter

        binding.mapFragmentSpinnerRange.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                removePing()
                removeCampingMarker()
                visible = true
                when (position) {
                    0 -> {
                        range = 50.0
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

                    }
                    1 -> {
                        range = 30.0
                        if(currentLat == 35.869326) {
                            showCustomToast("현재 위치를 받아올 수 없습니다.")
                        }
                        runBlocking {
                            mainViewModel.getPlacesByGps(currentLat, currentLng, range)
                        }
                        setCircleByRange(currentLat, currentLng)
                    }
                    2 -> {
                        range = 20.0
                        if(currentLat == 35.869326) {
                            showCustomToast("현재 위치를 받아올 수 없습니다.")
                        }
                        runBlocking {
                            mainViewModel.getPlacesByGps(currentLat, currentLng, range)
                        }
                        setCircleByRange(currentLat, currentLng)
                    }
                    3 -> {
                        range = 10.0
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

//        mapView.setCalloutBalloonAdapter(CustomBalloonAdapter(layoutInflater))  // 커스텀 말풍선 등록
        mapView.setPOIItemEventListener(eventListener)  // 마커 클릭 이벤트 리스너 등록

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
            marker.tag = -1
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


    /**
     * floating Button 클릭 이벤트(내 주변 캠핑장 정보)
     */
    private fun floatingBtnClickEvent() {
        binding.mapFragmentFabGetCamping.setOnClickListener {
            if(visible) {
                Log.d(TAG, "floatingBtnClickEvent: ${range * 1000}")
                runBlocking {
                    mainViewModel.getCampingList(currentLat, currentLng, (range * 1000).toInt())
                }
                setCampingPlaceMarker()
                visible = false
            } else {
                removeCampingMarker()
                visible = true
            }
        }
    }

    /**
     * 주변 캠핑장 마커 표시
     */
    private fun setCampingPlaceMarker() {
        removeCampingMarker()

        val markerList = arrayListOf<MapPoint>()
        val campingList = mainViewModel.campingList.value
        if(campingList != null && campingList.isNotEmpty()) {
        Log.d(TAG, "setCampingPlaceMarker: ${campingList.size}")
            for(item in campingList){
                val mapPoint = MapPoint.mapPointWithGeoCoord(item.mapY, item.mapX)
                markerList.add(mapPoint)
            }

            for(i in 0 until markerList.size){
                val marker = MapPOIItem()
                marker.itemName = campingList[i].facltNm
                marker.mapPoint = markerList[i]
                marker.markerType = MapPOIItem.MarkerType.YellowPin
                marker.tag = i
                campingPOIList.add(marker)
            }
            mapView.addPOIItems(campingPOIList.toArray(arrayOfNulls(campingPOIList.size)))
            Log.d(TAG, "setCampingPlaceMarker: ${campingPOIList.size} / ${markerList.size} / ${campingList.size}")
        } else {
            showCustomToast("사용자 주변에 캠핑 장소가 없습니다.")
        }
    }

    /**
     * 캠핑장 정보 마커 삭제
     */
    private fun removeCampingMarker() {
        if(campingPOIList.isNotEmpty()) {
            mapView.removePOIItems(campingPOIList.toArray(arrayOfNulls(campingPOIList.size)))
            campingPOIList = arrayListOf()
        }
    }



//    단말의 현위치 좌표값을 통보받을 수 있다.
    override fun onCurrentLocationUpdate(p0: MapView, p1: MapPoint, p2: Float) {
//        removePing()
//        visible = true

        val mapPointGeo: GeoCoordinate = p1.mapPointGeoCoord
        currentLat = mapPointGeo.latitude
        currentLng = mapPointGeo.longitude
        runBlocking {
            mainViewModel.getPlacesByGps(mapPointGeo.latitude, mapPointGeo.longitude, range)
        }
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






    // 커스텀 말풍선 클래스
    inner class CustomBalloonAdapter(inflater: LayoutInflater): CalloutBalloonAdapter {
        val mCalloutBalloon: View = inflater.inflate(R.layout.map_balloon_layout, null)
        val name: TextView = mCalloutBalloon.findViewById(R.id.ball_tvName)
        val address: TextView = mCalloutBalloon.findViewById(R.id.ball_tvAddress)

        override fun getCalloutBalloon(poiItem: MapPOIItem?): View {
            // 마커 클릭 시 나오는 말풍선
            name.text = poiItem?.itemName
//            address.text = poiItem.
            return mCalloutBalloon
        }

        override fun getPressedCalloutBalloon(poiItem: MapPOIItem?): View {
            // 말풍선 클릭 시
//            address.text = "getPressedCalloutBalloon"
            return mCalloutBalloon
        }
    }


    // 마커 클릭 이벤트 리스너
    inner class MarkerEventListener(val context: Context, val viewModel: MainViewModel): MapView.POIItemEventListener {
        private val TAG = "LocationMapFragment"
        override fun onPOIItemSelected(mapView: MapView?, poiItem: MapPOIItem?) {
            // 마커 클릭 시
        }

        override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?) {
            // 말풍선 클릭 시 (Deprecated)
            // 이 함수도 작동하지만 그냥 아래 있는 함수에 작성하자
        }

        override fun onCalloutBalloonOfPOIItemTouched(mapView: MapView?, poiItem: MapPOIItem?, buttonType: MapPOIItem.CalloutBalloonButtonType?) {

            if(poiItem!!.tag != -1) {

                val campingList = viewModel.campingList.value

                // 마커 아이템 불러오기
                val idx = poiItem.tag
                val item = campingList!![idx]

                // 말풍선 클릭 시
                val dialog = Dialog(context)
                val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_place_info,null)
                dialog.setContentView(dialogView)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

                val params = dialog.window?.attributes
                params?.width = WindowManager.LayoutParams.MATCH_PARENT
                dialog.window?.attributes = params

                dialog.show()


                val phone = dialogView.findViewById<ImageView>(R.id.placeInfoDialog_ivPlacePhone)
                val homePage = dialogView.findViewById<ImageView>(R.id.placeInfoDialog_ivPlaceHomePage)

                // phone 클릭하면 핸드폰 전화 이동
                phone.setOnClickListener {
                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${item.tel}")))
                }
                // homepage 클릭하면 웹으로 이동
                homePage.setOnClickListener {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.homepage)))
                }

                Log.d(TAG, "onCalloutBalloonOfPOIItemTouched: $item.firstImageUrl")
//                 이미지
                if(item.firstImageUrl.isEmpty()) {
                    dialogView.findViewById<ImageView>(R.id.placeInfoDialog_ivPlaceImg).visibility = View.GONE
                } else {
                    dialogView.findViewById<ImageView>(R.id.placeInfoDialog_ivPlaceImg).visibility = View.VISIBLE

                    Glide.with(dialogView)
                        .load(item.firstImageUrl)
                        .into(dialogView.findViewById<ImageView>(R.id.placeInfoDialog_ivPlaceImg))
                }
//                Glide.with(dialogView)
//                    .load(item.firstImageUrl)
//                    .into(dialogView.findViewById<ImageView>(R.id.placeInfoDialog_ivPlaceImg))

                dialogView.findViewById<TextView>(R.id.placeInfoDialog_tvPlaceInduty).text = "[${item.induty}]"   // 업종
                dialogView.findViewById<TextView>(R.id.placeInfoDialog_tvPlaceName).text = item.facltNm   // 장소 이름
                dialogView.findViewById<TextView>(R.id.placeInfoDialog_tvPlaceAddr).text = item.addr1    // 주소
                dialogView.findViewById<TextView>(R.id.placeInfoDialog_tvPlaceAddrDetail).text = item.addr2 // 주소 상세
                dialogView.findViewById<TextView>(R.id.placeInfoDialog_tvPlaceLineIntro).text = item.lineIntro // 한 줄 소개

                dialogView.findViewById<AppCompatButton>(R.id.placeInfoDialog_btnClose).setOnClickListener {
                    dialog.dismiss()
                }
            }


//            val builder = AlertDialog.Builder(context)
//            val itemList = arrayOf("토스트", "마커 삭제", "취소")
//            builder.setTitle("${poiItem?.itemName}")
//            builder.setItems(itemList) { dialog, which ->
//                when(which) {
//                    0 -> Toast.makeText(context, "토스트", Toast.LENGTH_SHORT).show()  // 토스트
//                    1 -> mapView?.removePOIItem(poiItem)    // 마커 삭제
//                    2 -> dialog.dismiss()   // 대화상자 닫기
//                }
//            }
//            builder.show()
        }

        override fun onDraggablePOIItemMoved(mapView: MapView?, poiItem: MapPOIItem?, mapPoint: MapPoint?) {
            // 마커의 속성 중 isDraggable = true 일 때 마커를 이동시켰을 경우
        }
    }
}