package com.whitebear.travel.src.main.home

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kakao.kakaonavi.*
import com.kakao.kakaonavi.options.CoordType
import com.kakao.kakaonavi.options.RpOption
import com.kakao.kakaonavi.options.VehicleType
import com.whitebear.travel.R
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentNavigatorBinding
import com.whitebear.travel.src.main.MainActivity
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapPolyline
import net.daum.mf.map.api.MapView
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "NavigatorFragment"
class NavigatorFragment : BaseFragment<FragmentNavigatorBinding>(FragmentNavigatorBinding::bind,R.layout.fragment_navigator) {
    private lateinit var mapView:MapView
    var markerArr = arrayListOf<MapPoint>()
    private lateinit var mainActivity:MainActivity
    private lateinit var navAdapter : NavPlaceAdapter

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
        mainActivity.hideBottomNav(true)
        binding.viewModel = mainViewModel

        setListener()
    }
    fun setListener(){
        initButtons()
        initMapView()
    }
    fun initButtons(){
        binding.fragmentNavigatorBackBtn.setOnClickListener {
            this@NavigatorFragment.findNavController().popBackStack()
        }

        binding.fragmentNavigatorNav.setOnClickListener {
            if(!markerArr.isEmpty()){
                showSelectType()
            }

        }
    }
    fun initMapView(){

        mapView = MapView(requireContext())
        if(mapView.parent != null){
            (mapView.parent as ViewGroup).removeView(mapView)
        }

        var mapViewContainer = binding.fragmentNavigatorKakaoMap as ViewGroup
        mapViewContainer.addView(mapView)
        if(mainViewModel.liveNavBucketList.value!!.isEmpty()){
            mainViewModel.userLoc.observe(viewLifecycleOwner, {
                var mapPoint = MapPoint.mapPointWithGeoCoord(it.latitude, it.longitude)
                mapView.setMapCenterPoint(mapPoint, true)
                mapView.setZoomLevel(6,true)
            })
        }else{
            var first = mainViewModel.liveNavBucketList.value!![0]
            var mapPoint = MapPoint.mapPointWithGeoCoord(first.lat, first.long)
            mapView.setMapCenterPoint(mapPoint, true)
            mapView.setZoomLevel(6,true)
            initAdapter()
            addPing()
        }

    }
    private fun initAdapter(){
        mainViewModel.liveNavBucketList.observe(viewLifecycleOwner, {
            navAdapter = NavPlaceAdapter()
            navAdapter.list = it
            binding.fragmentNavigatorPlaceRv.apply {
                layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
                adapter = navAdapter
                adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
        })
    }
    private fun addPing(){
        markerArr = arrayListOf()
        mainViewModel.liveNavBucketList.observe(viewLifecycleOwner, {
            for(item in 0..it.size-1){
                val mapPoint = MapPoint.mapPointWithGeoCoord(it[item].lat, it[item].long)
                markerArr.add(mapPoint)
            }
            setPing(markerArr)
            addPolyLine(markerArr)
        })
    }
    private fun setPing(markerArr : ArrayList<MapPoint>) {
        removePing()
        val list = arrayListOf<MapPOIItem>()
        for(i in 0..markerArr.size-1){
            val marker = MapPOIItem()
            marker.itemName = mainViewModel.liveNavBucketList.value!![i].name
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
    private fun addPolyLine(markerArr:ArrayList<MapPoint>) {
        val polyLine = MapPolyline()
        polyLine.tag = 1000
        polyLine.lineColor = Color.parseColor("#2054B3")
        polyLine.addPoints(markerArr.toArray(arrayOfNulls(markerArr.size)))
        mapView.addPolyline(polyLine)
    }
    fun goNavi(markerArr:ArrayList<MapPoint>, flag:Int){
        markerArr[0].mapPointGeoCoord.latitude
        try {
            if (KakaoNaviService.isKakaoNaviInstalled(requireContext())) {
                val kakao: com.kakao.kakaonavi.Location =
                    Destination.newBuilder("destination", markerArr[0].mapPointGeoCoord.longitude,markerArr[0].mapPointGeoCoord.latitude).build()
                val stops = LinkedList<Location>()
                for(i in 1..markerArr.size-1){
                    val stop = com.kakao.kakaonavi.Location.newBuilder("출발",markerArr[i].mapPointGeoCoord.longitude,markerArr[i].mapPointGeoCoord.latitude).build()
                    stops.add(stop)
                }
                if(flag == 1){
                    val params = KakaoNaviParams.newBuilder(kakao)
                        .setNaviOptions(
                            NaviOptions.newBuilder()
                                .setCoordType(CoordType.WGS84) // WGS84로 설정해야 경위도 좌표 사용 가능.
                                .setStartAngle(200) //시작 앵글 크기 설정.
                                .setVehicleType(VehicleType.FIRST).build()
                        ).setViaList(stops).build() //길 안내 차종 타입 설정

                    KakaoNaviService.navigate(requireContext(), params)
                }else if(flag==2){
                    val params = KakaoNaviParams.newBuilder(kakao)
                        .setNaviOptions(
                            NaviOptions.newBuilder()
                                .setCoordType(CoordType.WGS84) // WGS84로 설정해야 경위도 좌표 사용 가능.
                                .setStartAngle(200) //시작 앵글 크기 설정.
                                .setVehicleType(VehicleType.TWO_WHEEL).build()
                        ).setViaList(stops).build() //길 안내 차종 타입 설정

                    KakaoNaviService.navigate(requireContext(), params)
                }


            } else {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.locnall.KimGiSa")
                )
                Log.e(TAG, "showNaviKakao: 네비 설치 안됨")
                startActivity(intent)
            }
        } catch (e: Exception) {
            Log.e("네비연동 에러", e.toString() + "")
        }
    }
    fun showSelectType(){
        var dialog = Dialog(requireContext())
        var dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_nav_type,null)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        dialogView.findViewById<ConstraintLayout>(R.id.fragment_navigator_typeCar).setOnClickListener {
            //type : 1
            if(!markerArr.isEmpty()){
                goNavi(markerArr, 1)
            }else{
                showCustomToast("네비게이터에 추가할 장소가 없습니다.")
            }

        }
        dialogView.findViewById<ConstraintLayout>(R.id.fragment_naviator_typeBicycle).setOnClickListener {
            //type : 2
            if(!markerArr.isEmpty()){
                goNavi(markerArr, 2)
            }else{
                showCustomToast("네비게이터에 추가할 장소가 없습니다.")
            }
        }

    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NavigatorFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}