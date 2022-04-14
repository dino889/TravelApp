package com.whitebear.travel.src.main.place

import android.animation.ValueAnimator
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
import com.whitebear.travel.src.dto.Message
import com.whitebear.travel.src.dto.PlaceLike
import com.whitebear.travel.src.network.service.PlaceService
import kotlinx.coroutines.runBlocking
import retrofit2.Response

private const val TAG = "PlaceDetailFragment"
class PlaceDetailFragment : BaseFragment<FragmentPlaceDetailBinding>(FragmentPlaceDetailBinding::bind,R.layout.fragment_place_detail) {
    private var placeId = 0
    private var heartFlag = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            placeId = getInt("placeId")
            Log.d(TAG, "onCreate: $placeId")
            heartFlag = getBoolean("heartFlag")
            Log.d(TAG, "onCreate: $heartFlag")
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
        if(heartFlag){
            binding.fragmentPlaceDetailHeart.progress = 0.5f
        }else{
            binding.fragmentPlaceDetailHeart.progress = 0f
        }
        initTabLayout()
        initButtons()
    }

    fun initButtons(){
        binding.fragmentPlaceDetailAppBarBack.setOnClickListener {
            this@PlaceDetailFragment.findNavController().popBackStack()
        }
        binding.fragmentPlaceDetailAddBucket.setOnClickListener {
            if(mainViewModel.liveNavBucketList.value!!.size < 4){
                var place = mainViewModel.place.value!!
                mainViewModel.insertPlaceShopList(place)
                showCustomToast("추가되었습니다.")
                binding.fragmentPlaceDetailLottie.playAnimation()
            }else{
                showCustomToast("더이상 추가하실 수 없습니다.")
            }
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
            Log.d(TAG, "likePlace: ${response.code()}")
            val res = response.body()
            if(res != null){
                Log.d(TAG, "likePlace: ${res}")
                if(res.isSuccess){
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
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlaceDetailFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}