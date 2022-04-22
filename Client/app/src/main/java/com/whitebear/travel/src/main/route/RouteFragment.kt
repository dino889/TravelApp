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
import androidx.databinding.DataBindingUtil
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
import com.whitebear.travel.src.dto.Keyword
import com.whitebear.travel.src.dto.Message
import com.whitebear.travel.src.dto.RouteLike
import com.whitebear.travel.src.main.MainActivity
import com.whitebear.travel.src.network.service.RouteService
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import java.text.SimpleDateFormat
import android.view.ViewGroup

import android.view.LayoutInflater
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil.setContentView

import androidx.databinding.ViewDataBinding
import com.whitebear.travel.databinding.DialogRouteDetailBinding
import okhttp3.internal.notify


private const val TAG = "RouteFragment"
class RouteFragment : BaseFragment<FragmentRouteBinding>(FragmentRouteBinding::bind,R.layout.fragment_route) {
    private lateinit var mainActivity:MainActivity
    private lateinit var routeAdapter: RouteAdapter
    private var areaName = "대구"
    private var routeId = 0
    private var heartFlag = false
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
        setListener()

        if(routeId > 0){
            showDialogDetailRoute(routeId, heartFlag)
        }
    }
    private fun setListener(){
        initAdapter()
        initTabLayout()
        initSpinner()
    }
    private fun initTabLayout(){
        var areas = mainViewModel.areas.value!!
        for(item in 0..areas.size-1){
            binding.fragmentRouteTabLayout.addTab(binding.fragmentRouteTabLayout.newTab().setText(areas[item].name))
        }

        routeAdapter = RouteAdapter(mainViewModel)
        binding.fragmentRouteTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.fragmentRouteFilterSpinner.setSelection(0)
                if(tab != null){
                    areaName = tab?.text.toString()
                }
                runBlocking {
                    mainViewModel.getRoutes(tab?.text.toString())
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

        mainViewModel.routesLikes.observe(viewLifecycleOwner) {
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
            override fun onClick(view: View, position: Int, routeId: Int, heartFlag: Boolean) {
                showDialogDetailRoute(routeId,heartFlag)
            }

        })
        routeAdapter.filter.filter("")
        binding.fragmentRouteSv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                var curTime = System.currentTimeMillis()
                var formatter = SimpleDateFormat("yyyy-MM-dd HH:ss")
                var nows = formatter.format(curTime)
                if(query!=null){
                    var keywords = Keyword(
                        query,
                        "경로",
                        nows
                    )
                    mainViewModel.insertKeywords(keywords)
                    return false
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(TextUtils.isEmpty(newText)){
                    routeAdapter.filter.filter("")
                }else{
                    routeAdapter.filter.filter(newText.toString())
                    routeAdapter.notifyDataSetChanged()
                }
                return false
            }

        })
    }
    private fun showDialogDetailRoute(id:Int,heartFlag:Boolean) {
        mainViewModel.getRoute(id)
        val dialog = Dialog(requireContext())
        val binding: DialogRouteDetailBinding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()), R.layout.dialog_route_detail, null, false)
        dialog.setContentView(binding.root)
        val route = mainViewModel.route.value!!

        mainViewModel.route.observe(viewLifecycleOwner, {
            binding.route = it
            dialog.onContentChanged()
        })

//        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_route_detail,null)
//        dialog.setContentView(dialogView)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val params = dialog.window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window?.attributes = params
        dialog.show()

        dialog.setOnDismissListener {
            runBlocking {
                mainViewModel.getRoutesLikes(ApplicationClass.sharedPreferencesUtil.getUser().id)
            }
            routeAdapter.notifyDataSetChanged()
        }


        binding.fragmentRouteDetailBack.setOnClickListener {
            dialog.dismiss()
        }

        val heart = binding.fragmentRouteDetailLike
        if(heartFlag){
            heart.progress = 0.4f
        }else{
            heart.progress = 0f
        }
        heart.setOnClickListener {
            //좋아요 해라
            val routeLike = RouteLike(
                ApplicationClass.sharedPreferencesUtil.getUser().id,
                id
            )
            likeRoute(id, routeLike,heart)
        }

        Glide.with(binding.root)
            .load(route.imgURL)
            .into(binding.fragmentRouteDetailImg)

       runBlocking {
           mainViewModel.getRoutesInPlaceArr(route.placeIdList)
       }

        val routeDetailAdapter = RouteDetailAdapter()
        mainViewModel.placesToRoutes.observe(viewLifecycleOwner) {
            routeDetailAdapter.list = it
        }

        binding.fragmentRouteDetailPlaceRv.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter = routeDetailAdapter
            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        binding.fragmentRouteDetailAddBucket.setOnClickListener {
            val places = mainViewModel.placesToRoutes.value!!
            if(mainViewModel.liveNavBucketList.value!!.size > 4){
                showCustomToast("더이상 추가하실 수 없습니다.")
            }else{
                for(item in places){
                    mainViewModel.insertPlaceShopList(item)
                }
                showCustomToast("추가되었습니다.")
            }
        }
    }

    private fun likeRoute(id: Int, routeLike:RouteLike,heart : LottieAnimationView){
        var response : Response<Message>
        runBlocking {
            response = RouteService().routeLike(routeLike)
        }
        if(response.code() == 200 || response.code() == 500 || response.code() == 201){
            val res = response.body()
            if(res!=null){
                if(res.isSuccess){
                    runBlocking {
                        mainViewModel.getRoutes(areaName)
                    }
                    mainViewModel.getRoute(id)
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
    
    private fun initSpinner(){
        var  spinnerArr = arrayListOf<String>("별점순","리뷰 적은순","리뷰 많은순")
        val adapter = ArrayAdapter(requireContext(),R.layout.support_simple_spinner_dropdown_item,spinnerArr)
        binding.fragmentRouteFilterSpinner.adapter = adapter

        binding.fragmentRouteFilterSpinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(position == 0){
                    runBlocking {
                        mainViewModel.getRoutes(areaName)
                    }
                }
                if(position == 1){
                    runBlocking {
                        mainViewModel.getRoutesToSort(areaName,"review")
                    }
                }
                if(position == 2){
                    runBlocking {
                        mainViewModel.getRoutesToSort(areaName,"review_asc")
                    }
                }

                initAdapter()
                routeAdapter.notifyDataSetChanged()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }

}