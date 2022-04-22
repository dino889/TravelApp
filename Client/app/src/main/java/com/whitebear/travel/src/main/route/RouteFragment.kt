package com.whitebear.travel.src.main.route

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.jakewharton.rxbinding3.material.selections
import com.whitebear.travel.R
import com.whitebear.travel.config.ApplicationClass
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentRouteBinding
import com.whitebear.travel.src.dto.*
import com.whitebear.travel.src.main.MainActivity
import com.whitebear.travel.src.network.service.RouteService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import java.text.SimpleDateFormat

private const val TAG = "RouteFragment"
class RouteFragment : BaseFragment<FragmentRouteBinding>(FragmentRouteBinding::bind,R.layout.fragment_route) {
    private lateinit var mainActivity:MainActivity
    private lateinit var routeAdapter: RouteAdapter
    private var areaName = "대구"
    private var routeId = 0
    private var heartFlag = false
    lateinit var navDao: NavDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            routeId = it.getInt("routeId")
            heartFlag = it.getBoolean("heartFlag")
            areaName = it.getString("areaName").toString()
       }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = mainViewModel
        runBlocking {
            if(areaName.equals("") || areaName == null || areaName.equals("null")){
                areaName = "대구"
            }
            mainViewModel.getRoutes(areaName)
            mainViewModel.getRoutesLikes(ApplicationClass.sharedPreferencesUtil.getUser().id)
        }
        navDao = mainActivity.navDB?.navDao()!!
        setListener()

        if(routeId > 0){
            showDialogDetailRoute(routeId, heartFlag)
        }
    }
    private fun setListener(){
        initAdapter()
        initTabLayout()
    }
    private fun initTabLayout(){
        var areas = mainViewModel.areas.value!!
        for(item in 0..areas.size-1){
            binding.fragmentRouteTabLayout.addTab(binding.fragmentRouteTabLayout.newTab().setText(areas[item].name))
        }

        routeAdapter = RouteAdapter(mainViewModel)
        binding.fragmentRouteTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                var tabName = ""
                if(tabName.length < 4){
                    tabName = tab?.text.toString().substring(0,2)
                }else{
                    tabName = tab?.text.toString().substring(0,4)
                }
                if (tab != null) {
                    areaName = tabName
                }

                runBlocking {
                    mainViewModel.getRoutes(tabName)
                }
                initAdapter()
                routeAdapter.notifyDataSetChanged()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

    }
    private fun initAdapter(){
        routeAdapter = RouteAdapter(mainViewModel)
        routeAdapter.list = mainViewModel.routes.value!!
        routeAdapter.filter.filter("")
        mainViewModel.routesLikes.observe(viewLifecycleOwner) {
            Log.d(TAG, "initAdapter: $it")
            routeAdapter.likeList = it
        }

        mainViewModel.routes.observe(viewLifecycleOwner) {
            routeAdapter.list = it
        }

        binding.fragmentRouteRv.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
            adapter = routeAdapter
            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        routeAdapter.setOnItemClickListener(object: RouteAdapter.ItemClickListener{
            override fun onClick(view: View, position: Int, routeId: Int, heartFlag: Boolean, areaName:String) {
                showDialogDetailRoute(routeId,heartFlag)
            }
        })
    }
    private fun showDialogDetailRoute(id:Int,heartFlag:Boolean){
        Log.d(TAG, "onViewCreated: $id  $heartFlag")
        mainViewModel.getRoute(id)
        var dialog = Dialog(requireContext())
        var dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_route_detail,null)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        var params = dialog.window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window?.attributes = params
        dialog.show()
        dialogView.findViewById<ImageButton>(R.id.fragment_route_detailBack).setOnClickListener {
            dialog.dismiss()
        }
        var heart = dialogView.findViewById<LottieAnimationView>(R.id.fragment_route_detailLike)
        if(heartFlag){
            heart.progress = 0.4f
        }else{
            heart.progress = 0f
        }
        heart.setOnClickListener {
            //좋아요 해라
            var routeLike = RouteLike(
                ApplicationClass.sharedPreferencesUtil.getUser().id,
                id
            )
            likeRoute(routeLike,heart)
        }

        var route = mainViewModel.route.value!!

        dialogView.findViewById<TextView>(R.id.fragment_route_detailName).text = route.name
        dialogView.findViewById<TextView>(R.id.fragment_route_detailContent).text = route.description
        dialogView.findViewById<TextView>(R.id.fragment_route_detailReview).text = route.rating.toFloat().toString()
        dialogView.findViewById<TextView>(R.id.fragment_route_detailLikeCnt).text = route.heartCount.toString()

        Glide.with(dialogView)
            .load(route.imgURL)
            .into(dialogView.findViewById<ImageView>(R.id.fragment_route_detailImg))

       runBlocking {
           mainViewModel.getRoutesInPlaceArr(route.placeIdList)
       }
        var routeDetailAdapter = RouteDetailAdapter()
        mainViewModel.placesToRoutes.observe(viewLifecycleOwner) {
            routeDetailAdapter.list = it
        }
        dialogView.findViewById<RecyclerView>(R.id.fragment_route_detailPlaceRv).apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter = routeDetailAdapter
            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        dialogView.findViewById<ConstraintLayout>(R.id.fragment_routeDetail_addBucket).setOnClickListener {
            var places = mainViewModel.placesToRoutes.value!!
            var placeList = mutableListOf<Navigator>()
            var userId = ApplicationClass.sharedPreferencesUtil.getUser().id
            val job1 = CoroutineScope(Dispatchers.IO).launch {
                placeList = navDao.getNav(userId) as MutableList<Navigator>
            }
            runBlocking {
                job1.join()
            }
            if(placeList.size == 0){
                for(item in places){
                    val job = CoroutineScope(Dispatchers.IO).launch {
                        navDao.insertNav(Navigator(0,userId, item.id,item.name,item.lat,item.long,item.address,item.summary,item.imgURL))
                    }
                    runBlocking {
                        job.join()
                    }
                    showCustomToast("추가되었습니다.")
                }
            }else if(placeList.size > 0 && placeList.size < 4){
                var size1 = places.size
                var size2 = placeList.size
                var resultSize = size1 - size2
                for(item in 0..resultSize){
                    val job = CoroutineScope(Dispatchers.IO).launch {
                        navDao.insertNav(Navigator(0,userId, places[item].id,places[item].name,places[item].lat,places[item].long,places[item].address,places[item].summary,places[item].imgURL))
                    }
                    runBlocking {
                        job.join()
                    }
                    showCustomToast("추가되었습니다.")
                }
            }else{
                showCustomToast("더이상 추가하실 수 없습니다.")
            }





//
//            if(mainViewModel.liveNavBucketList.value!!.size > 4){
//                showCustomToast("더이상 추가하실 수 없습니다.")
//            }else{
//                for(item in places){
//                    mainViewModel.insertPlaceShopList(item)
//                }
//                showCustomToast("추가되었습니다.")
//            }
        }

    }
    private fun likeRoute(routeLike:RouteLike,heart : LottieAnimationView){
        var response : Response<Message>
        runBlocking {
            response = RouteService().routeLike(routeLike)
        }
        if(response.code() == 200 || response.code() == 500 || response.code() == 201){
            val res = response.body()
            if(res!=null){
                if(res.isSuccess){
                    if(!res.message.contains("취소")){
                        val animator = ValueAnimator.ofFloat(0f,0.4f).setDuration(500)
                        animator.addUpdateListener { animation ->
                            heart.progress = animation.animatedValue as Float
                        }
                        animator.start()
                    }else{
                        val animator = ValueAnimator.ofFloat(0.4f,0f).setDuration(500)
                        animator.addUpdateListener { animation ->
                            heart.progress = animation.animatedValue as Float
                        }
                        animator.start()
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RouteFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}