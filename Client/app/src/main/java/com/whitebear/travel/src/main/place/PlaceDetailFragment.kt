package com.whitebear.travel.src.main.place

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.whitebear.travel.R
import com.whitebear.travel.config.ApplicationClass
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentPlaceDetailBinding
import com.whitebear.travel.src.dto.*
import com.whitebear.travel.src.main.MainActivity
import com.whitebear.travel.src.network.service.PlaceService
import kotlinx.coroutines.*
import retrofit2.Response
import java.lang.Runnable

private const val TAG = "PlaceDetailFragment"
class PlaceDetailFragment : BaseFragment<FragmentPlaceDetailBinding>(FragmentPlaceDetailBinding::bind,R.layout.fragment_place_detail) {
    private lateinit var mainActivity : MainActivity

    private var placeId = 0
    private var heartFlag = false
    lateinit var navDao:NavDao

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            placeId = getInt("placeId")
            Log.d(TAG, "onCreate: $placeId")
            heartFlag = getBoolean("heartFlag")
            Log.d(TAG, "onCreate: $heartFlag")
        }
        mainActivity.hideBottomNav(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = mainViewModel

        runBlocking {
            mainViewModel.getPlace(placeId)
        }
        navDao = mainActivity.navDB?.navDao()!!

        mainViewModel.place.observe(viewLifecycleOwner, {
            binding.place = it
        })

        setListener()
    }
    fun setListener(){
        if(heartFlag){
            binding.fragmentPlaceDetailHeart.progress = 0.5f
        }else{
            binding.fragmentPlaceDetailHeart.progress = 0f
        }
        initTabLayout()
        initButtons()
    }

    fun initButtons() {
        binding.fragmentPlaceDetailAppBarBack.setOnClickListener {
            this@PlaceDetailFragment.findNavController().popBackStack()
        }
        binding.fragmentPlaceDetailAddBucket.setOnClickListener {
            var place = mainViewModel.place.value!!
            var size = 0
            var placeList = mutableListOf<Place>()
            val job1 = CoroutineScope(Dispatchers.IO).launch {
                placeList = navDao.getNav(ApplicationClass.sharedPreferencesUtil.getUser().id) as MutableList<Place>
            }
            runBlocking {
                job1.join()
            }

            if(placeList.size < 4){
                val job = CoroutineScope(Dispatchers.IO).launch {
                    navDao.insertNav(Navigator(0,ApplicationClass.sharedPreferencesUtil.getUser().id,place.id,place.name,place.lat,place.long,place.address,place.summary,place.imgURL))
                }
                runBlocking {
                    job.join()
                }
                showCustomToast("추가되었습니다.")
            }else{
                showCustomToast("더이상 추가하실 수 없습니다.")
            }



//            if(mainViewModel.liveNavBucketList.value!!.size < 4){
//                var place = mainViewModel.place.value!!
//                mainViewModel.insertPlaceShopList(place)
//                showCustomToast("추가되었습니다.")
//                binding.fragmentPlaceDetailLottie.playAnimation()
//            }else{
//                showCustomToast("더이상 추가하실 수 없습니다.")
//            }
        }
        binding.fragmentPlaceDetailHeart.setOnClickListener {
            var placeLike = PlaceLike(
                ApplicationClass.sharedPreferencesUtil.getUser().id,
                placeId
            )
            likePlace(placeLike)
        }
    }

    private fun likePlace(placeLike:PlaceLike){
        var response : Response<Message>
        runBlocking {
            response = PlaceService().placeLike(placeLike)
        }
        if(response.code() == 200 || response.code() == 500 || response.code() == 201){
            val res = response.body()
            if(res != null){
                if(res.isSuccess){
                    runBlocking {
                        mainViewModel.getPlace(placeId)
                    }
                    if(!res.message.contains("취소")){
                        val animator = ValueAnimator.ofFloat(0f,0.4f).setDuration(500)
                        animator.addUpdateListener { animation ->
                            binding.fragmentPlaceDetailHeart.progress = animation.animatedValue as Float
                        }
                        animator.start()

                    }else{
                        val animator = ValueAnimator.ofFloat(1f,0f).setDuration(500)
                        animator.addUpdateListener { animation ->
                            binding.fragmentPlaceDetailHeart.progress = animation.animatedValue as Float
                        }
                        animator.start()
                    }
                }
            }
        }
    }
    fun initTabLayout(){
        val tabList = arrayListOf<String>("INFO","REVIEW")

        val pagerAdapter = PlaceDetailPagerAdapter(this)
        pagerAdapter.addFragment(PlaceInfoFragment.newInstance("placeId",placeId))
        pagerAdapter.addFragment(PlaceReviewFragment.newInstance("placeId",placeId))

        binding.fragmentPlaceDetailVp.adapter = pagerAdapter
        TabLayoutMediator(binding.fragmentPlaceDetailTabLayout, binding.fragmentPlaceDetailVp){ tab,position ->
            tab.text = tabList.get(position)
        }.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNav(false)
    }
}