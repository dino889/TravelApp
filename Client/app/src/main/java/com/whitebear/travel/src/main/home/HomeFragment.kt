package com.whitebear.travel.src.main.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.whitebear.travel.R
import com.whitebear.travel.config.ApplicationClass
import com.whitebear.travel.databinding.FragmentHomeBinding
import com.whitebear.travel.src.dto.Place
import com.whitebear.travel.src.dto.Route
import com.whitebear.travel.src.dto.Weather
import com.whitebear.travel.src.dto.corona.Corona
import com.whitebear.travel.src.main.MainActivity
import com.whitebear.travel.src.network.service.DataService
import com.whitebear.travel.src.network.viewmodel.MainViewModel
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.Exception

private const val TAG = "HomeFragment"
class HomeFragment: Fragment(){
//class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind,R.layout.fragment_home) {
    //Banner
    private var currentPosition = 0
    private var myHandler = MyHandler()
    private var intervalTime = 1500.toLong()
    private var list = mutableListOf(R.drawable.banner, R.drawable.bannerses,R.drawable.banner1)

    //Weather
    lateinit var weather:Weather.Item

    //adapter
    private lateinit var areaAdapter:AreaAdapter
    private lateinit var bestRoutesAdapter: BestRoutesAdapter
    private lateinit var bestPlaceAdapter: BestPlaceAdapter

    private lateinit var mainActivity:MainActivity
    private lateinit var binding : FragmentHomeBinding
    val mainViewModel : MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = mainViewModel


        runBlocking {
            val user = ApplicationClass.sharedPreferencesUtil.getUser().id
            mainViewModel.getUserInfo(user, true)
            mainViewModel.getAreas()
            mainViewModel.getPlaces("")
            mainViewModel.getRoutes("")
            mainViewModel.getRoutesLikes(user)
            mainViewModel.getPlaceLikes(user)

        }

        if (!mainActivity.checkLocationServicesStatus()) {
            mainActivity.showDialogForLocationServiceSetting()
        } else {
            mainActivity.checkRunTimePermission()
        }
        mainActivity.startLocationUpdates()

        mainViewModel.userLoc.observe(viewLifecycleOwner) {
            if (it != null) {
                runBlocking {
                    try {
                        mainViewModel.getWeather("JSON",10,1, mainActivity.getToday().toInt(),"0200","${it.latitude.toInt()}","${it.longitude.toInt()}")
                    } catch (e: Exception) {
                        Log.e(TAG, "onViewCreated: weather API response 오류 ${e.printStackTrace()}", )
                    }
                    mainViewModel.getNearbyCenter(it.latitude, it.longitude)
                }
                initWeather()
                initMeasure()
            }
        }

        getCorona()

        setListener()

//        getCovidState()
    }

    fun setListener(){
        mainActivity.hideBottomNav(false)
        initButton()
        initBanner()
        initAdapter()
        notiBtnClickEvent()
//        if(mainViewModel.userLoc.value != null){
//        }
    }

    fun initButton(){
        binding.fragmentHomeNavBtn.setOnClickListener {
            this@HomeFragment.findNavController().navigate(R.id.navigatorFragment)
        }
        binding.fragmentHomeSearch.setOnClickListener {
            this@HomeFragment.findNavController().navigate(R.id.searchFragment)
        }
    }

    private fun initAdapter(){
        areaAdapter = AreaAdapter()
        mainViewModel.areas.observe(viewLifecycleOwner) {
            areaAdapter.list = it
        }
        binding.fragmentHomeAreaRv.apply {
            layoutManager = GridLayoutManager(context,5)
            adapter = areaAdapter
            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        areaAdapter.setItemClickListener(object : AreaAdapter.ItemClickListener{
            override fun onClick(view: View, position: Int, areaName: String, areaId:Int) {
                var area = bundleOf("areaName" to areaName, "areaId" to areaId)
                this@HomeFragment.findNavController().navigate(R.id.areaFragment,area)
            }
        })

        bestRoutesAdapter = BestRoutesAdapter()
        mainViewModel.routesLikes.observe(viewLifecycleOwner){
            bestRoutesAdapter.likelist = it
        }
        mainViewModel.routes.observe(viewLifecycleOwner) {
            var arr = mutableListOf<Route>()
            for(item in 0..5){
                arr.add(it[item])
            }
            bestRoutesAdapter.list = arr
        }
        binding.fragmentHomeBestRouteRv.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter= bestRoutesAdapter
            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        bestRoutesAdapter.setOnItemClickListenenr(object: BestRoutesAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, routeId: Int, heartFlag:Boolean, areaName : String) {
                var route = bundleOf("routeId" to routeId, "heartFlag" to heartFlag, "areaName" to areaName)
                this@HomeFragment.findNavController().navigate(R.id.routeFragment, route)
            }

        })

        bestPlaceAdapter = BestPlaceAdapter()
        mainViewModel.places.observe(viewLifecycleOwner) {
            var arr= mutableListOf<Place>()
            for(item in 0..5){
                arr.add(it[item])
            }
            bestPlaceAdapter.list = arr
        }

        mainViewModel.placeLikes.observe(viewLifecycleOwner){
            bestPlaceAdapter.likelist = it
        }

        binding.fragmentBestPlaceRv.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter= bestPlaceAdapter
            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        bestPlaceAdapter.setOnItemClickListenenr(object : BestPlaceAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, placeId: Int, heartFlag: Boolean) {
                var place = bundleOf("placeId" to placeId, "heartFlag" to heartFlag)
                Log.d(TAG, "onClick: $placeId / $heartFlag")
                this@HomeFragment.findNavController().navigate(R.id.placeDetailFragment,place)
            }

        })
    }

    private fun initWeather() {
//        val userLoc = mainViewModel.userLoc.value
//        if(userLoc != null) {
            mainViewModel.weathers.observe(viewLifecycleOwner) {
                binding.homeFragmentTvFailLoc.visibility = View.INVISIBLE
                binding.fragmentHomeWeatherSKY.visibility = View.VISIBLE
                binding.fragmentHomeWeatherTMP.visibility = View.VISIBLE
                binding.fragmentHomePm10.visibility = View.VISIBLE
                binding.fragmentHomePm25.visibility = View.VISIBLE

                if(it.response.body != null) {

                    val curWeather = it.response.body.items.item
                    for (item in 0..curWeather.size - 1) {
                        if (curWeather[item].category.equals("SKY")) {
                            if (curWeather[item].fcstValue.equals("1")) {
                                Glide.with(this)
                                    .load(R.drawable.weather1)
                                    .into(binding.fragmentHomeWeatherSKY)
                            } else if (curWeather[item].fcstValue.equals("2")) {
                                Glide.with(this)
                                    .load(R.drawable.weather2)
                                    .into(binding.fragmentHomeWeatherSKY)
                            } else if (curWeather[item].fcstValue.equals("3")) {
                                Glide.with(this)
                                    .load(R.drawable.weather3)
                                    .into(binding.fragmentHomeWeatherSKY)
                            } else if (curWeather[item].fcstValue.equals("4")) {
                                Glide.with(this)
                                    .load(R.drawable.weather4)
                                    .into(binding.fragmentHomeWeatherSKY)
                            }
                        }
                        if (curWeather[item].category.equals("T3H") || curWeather[item].category.equals("T1H") || curWeather[item].category.equals("TMP")) {
                            binding.fragmentHomeWeatherTMP.setText(curWeather[item].fcstValue + "℃")
                        }
                    }
                }

            }
//        }
    }

    private fun initBanner(){
        var banners = binding.fragmentHomeBanner
        banners.adapter = BannerAdapter(list)
        banners.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        banners.setCurrentItem(currentPosition,true)
        banners.apply {
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    when(state){
                        ViewPager2.SCROLL_STATE_IDLE -> autoScrollStart(intervalTime)
                        ViewPager2.SCROLL_STATE_DRAGGING -> autoScrollStop()
                    }
                }
            })
        }
    }

    private inner class MyHandler:Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if(msg.what == 0){
                binding.fragmentHomeBanner.setCurrentItem(++currentPosition%list.size, true)
                autoScrollStart(intervalTime)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initMeasure(){
        mainViewModel.coordinates.observe(viewLifecycleOwner) {
//            var tm = mainViewModel.coordinates.value!!
            var tm = it
            var tmX = tm.documents!![0]!!.x
            var tmY = tm.documents!![0]!!.y
            runBlocking {
                mainViewModel.getFindMyCenter(tmX!!, tmY!!)
            }
        }

        mainViewModel.stations.observe(viewLifecycleOwner) {
//            var station = mainViewModel.stations.value!!
            var station = it
            var stationName = station.response!!.body!!.stations?.get(0)!!.stationName

            runBlocking {
                mainViewModel.getAirQuality(stationName!!)
            }
        }


        mainViewModel.air.observe(viewLifecycleOwner) {
            var curAir = it.response!!.body!!.measuredValues?.get(0)
            Log.d(TAG, "initMeasure: $curAir")
            binding.fragmentHomePm10.text = "미세먼지 ${curAir!!.pm10Value}"
            binding.fragmentHomePm25.text = "초미세먼지 ${curAir!!.pm25Value} |"
        }
    }

    private fun autoScrollStart(intervalTime:Long){
        myHandler.removeMessages(0)
        myHandler.sendEmptyMessageDelayed(0,intervalTime)
    }

    private fun autoScrollStop(){
        myHandler.removeMessages(0)
    }

    /**
     * 코로나 확진자 현황 받아오는 함수
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCovidState() {
        val todayDate = LocalDate.now()
        val yesterdayDate = todayDate.minusDays(1)
        val dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd")
        val today = dateFormat.format(todayDate)
        val yesterday = dateFormat.format(yesterdayDate)

        Log.d(TAG, "getCovidState: $yesterday / $today")
        try {
            runBlocking {
//                var response = DataService().getCovidState(yesterday, today)
                var response = DataService().getCovidState("20220417", "20220418")
                Log.d(TAG, "getCovidState: $response")
            }
        } catch (e : Exception) {
            Log.e(TAG, "getCovidState: ${e.printStackTrace()} ${e.message}", )
        }
    }

    private fun notiBtnClickEvent() {
        binding.fragmentHomeNotification.setOnClickListener {
            this@HomeFragment.findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
        }
    }

    /**
     * 국내 코로나 확진 현황 조회
     * respone.body().지역 내
     * totalCase - 전체 확진자 수
     * newCase - today 확진자 수
     * death - 사망자 수
     *
     */
    private fun getCorona() {
        var response : Response<Corona>

        try {
            runBlocking {
                response = DataService().getCorona()
            }
            if(response.code() == 200) {
                val res = response.body()
                if(res != null) {
                    //busan
                    binding.busanNewCorona.text = "(+${res.busan.newCase})"
                    binding.busanTotalCorona.text = res.busan.totalCase
                    //ulsan
                    binding.ulsanNewCorona.text = "(+${res.ulsan.newCase})"
                    binding.ulsanTotalCorona.text = res.ulsan.totalCase
                    //daegu
                    binding.deaguNewCorona.text = "(+${res.daegu.newCase})"
                    binding.deaguTotalCorona.text = res.daegu.totalCase
                    //경남
                    binding.geongnamNewCorona.text = "(+${res.gyeongnam.newCase})"
                    binding.geongnamTotalCorona.text = res.gyeongnam.totalCase
                    //경북
                    binding.geongbukNewCorona.text = "(+${res.gyeongbuk.newCase})"
                    binding.geongbukTotalCorona.text = res.gyeongbuk.totalCase
                    //강원
                    binding.gangwonNewCorona.text = "(+${res.gangwon.newCase})"
                    binding.gangwonTotalCorona.text = res.gangwon.totalCase
                    //경기
                    binding.geonggiNewCorona.text = "(+${res.gyeonggi.newCase})"
                    binding.geonggiTotalCorona.text = res.gyeonggi.totalCase
                    //서울
                    binding.seoulNewCorona.text = "(+${res.seoul.newCase})"
                    binding.seoulTotalCorona.text = res.seoul.totalCase
                    //인천
                    binding.incheonNewCorona.text = "(+${res.incheon.newCase})"
                    binding.incheonTotalCorona.text = res.incheon.totalCase
                    //충북
                    binding.chungbukNewCorona.text = "(+${res.chungbuk.newCase})"
                    binding.chungbukTotalCorona.text = res.chungbuk.totalCase
                    //충남
                    binding.chungnamNewCorona.text = "(+${res.chungnam.newCase})"
                    binding.chungnamTotalCorona.text = res.chungnam.totalCase
                    //전북
                    binding.junbookNewCorona.text = "(+${res.jeonbuk.newCase})"
                    binding.junbookTotalCorona.text = res.jeonbuk.totalCase
                    //전남
                    binding.junnamNewCorona.text = "(+${res.jeonnam.newCase})"
                    binding.junnamTotalCorona.text = res.jeonnam.totalCase
                    //광주
                    binding.guangjuNewCorona.text = "(+${res.gwangju.newCase})"
                    binding.guangjuTotalCorona.text = res.gwangju.totalCase
                    //대전
                    binding.deajunNewCorona.text = "(+${res.daejeon.newCase})"
                    binding.deajunTotalCorona.text = "(+${ res.daejeon.totalCase })"
                    //세종
                    binding.sejongNewCorona.text = "(+${res.sejong.newCase})"
                    binding.sejongTotalCorona.text = res.sejong.totalCase

                }
            } else {
                Log.e(TAG, "getCorona: 서버 통신 조회 오류 ${response.message()}", )
            }
        } catch (e : Exception) {
            Log.e(TAG, "getCorona: 통신 오류 ${e.printStackTrace()}", )
        }

    }
}