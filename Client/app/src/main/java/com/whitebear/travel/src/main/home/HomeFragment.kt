package com.whitebear.travel.src.main.home

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.whitebear.travel.R
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentHomeBinding
import com.whitebear.travel.src.dto.Weather
import com.whitebear.travel.src.main.MainActivity
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val TAG = "HomeFragment"
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind,R.layout.fragment_home) {
    //Banner
    private var currentPosition = 0
    private var myHandler = MyHandler()
    private var intervalTime = 1500.toLong()
    private var list = mutableListOf(R.drawable.banner, R.drawable.banner1)

    //Weather
    lateinit var weather:Weather.Item

    //adapter
    private lateinit var areaAdapter:AreaAdapter

    private lateinit var mainActivity:MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
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
            mainViewModel.getAreas()
        }
        setListener()
    }
    fun setListener(){
        initButton()
        initBanner()
        initAdapter()
        initWeather()
    }
    fun initButton(){
        binding.fragmentHomeNavBtn.setOnClickListener {
            this@HomeFragment.findNavController().navigate(R.id.navigatorFragment)
        }
    }
    private fun initAdapter(){
        mainViewModel.areas.observe(viewLifecycleOwner, {
            areaAdapter = AreaAdapter()
            areaAdapter.list = it
            binding.fragmentHomeAreaRv.apply {
                layoutManager = GridLayoutManager(context,5)
                adapter = areaAdapter
                adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
        })
        

    }
    private fun initWeather(){
        mainViewModel.weathers.observe(viewLifecycleOwner, {
            var curWeather = it.response.body.items.item
            Log.d(TAG, "initWeather: $curWeather")
            var str = ""
            var temperature = ""
            for(item in 0.. curWeather.size-1){
//                var str = ""
                if(curWeather[item].category.equals("SKY")){
                    str += "현재 날씨는"
                    if(curWeather[item].fcstValue.equals("1")){
                        Glide.with(this)
                            .load(R.drawable.weather1)
                            .into(binding.fragmentHomeWeatherSKY)
                    }else if(curWeather[item].fcstValue.equals("2")){
                        Glide.with(this)
                            .load(R.drawable.weather2)
                            .into(binding.fragmentHomeWeatherSKY)
                    }else if(curWeather[item].fcstValue.equals("3")){
                        Glide.with(this)
                            .load(R.drawable.weather3)
                            .into(binding.fragmentHomeWeatherSKY)
                    }else if(curWeather[item].fcstValue.equals("4")){
                        Glide.with(this)
                            .load(R.drawable.weather4)
                            .into(binding.fragmentHomeWeatherSKY)
                    }
                }
//                var temperature = ""
                if(curWeather[item].category.equals("T3H") || curWeather[item].category.equals("T1H") || curWeather[item].category.equals("TMP")){
                    binding.fragmentHomeWeatherTMP.setText(curWeather[item].fcstValue + "℃")
                }
            }
        })

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

    private fun autoScrollStart(intervalTime:Long){
        myHandler.removeMessages(0)
        myHandler.sendEmptyMessageDelayed(0,intervalTime)
    }

    private fun autoScrollStop(){
        myHandler.removeMessages(0)
    }
    companion object {
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}