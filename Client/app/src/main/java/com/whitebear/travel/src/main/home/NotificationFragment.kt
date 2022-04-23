package com.whitebear.travel.src.main.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.whitebear.travel.R
import com.whitebear.travel.config.ApplicationClass
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentNotificationBinding
import com.whitebear.travel.src.dto.Notification
import com.whitebear.travel.src.main.MainActivity
import com.whitebear.travel.src.main.location.AroundPlaceAdapter
import com.whitebear.travel.src.network.service.NotificationService
import kotlinx.coroutines.runBlocking
import retrofit2.Response


class NotificationFragment : BaseFragment<FragmentNotificationBinding>(FragmentNotificationBinding::bind, R.layout.fragment_notification) {
    private val TAG = "NotificationFragment"
    private lateinit var mainActivity: MainActivity

    private lateinit var notiAdapter : NotificationAdapter
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.hideBottomNav(true)

        runBlocking {
            mainViewModel.getNotification(ApplicationClass.sharedPreferencesUtil.getUser().id)
        }

        initListener()
    }

    private fun initListener() {
        backBtnClickEvent()
        initAdapter()
        initSpinner()
    }

    private fun backBtnClickEvent() {
        binding.notiFragmentIvBack.setOnClickListener {
            this@NotificationFragment.findNavController().popBackStack()
        }
    }

    private fun initSpinner() {
        val type = arrayListOf("전체", "이벤트", "정보")
        val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line, type)
        binding.notiFragmentSpinnerCategory.adapter = adapter

        binding.notiFragmentSpinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {  // 전체
                        mainViewModel.notificationList.observe(viewLifecycleOwner, {
                            notiAdapter.list = it
                        })
                        notiAdapter.notifyDataSetChanged()
                    }
                    1 -> {  // 이벤트
                        mainViewModel.notificationList.observe(viewLifecycleOwner, {
                            val eventList = mutableListOf<Notification>()
                            for (item in it) {
                                if(item.type == "event") {
                                    eventList.add(item)
                                }
                            }
                            notiAdapter.list = eventList
                        })
                        notiAdapter.notifyDataSetChanged()
                    }
                    2 -> {  // 정보
                        mainViewModel.notificationList.observe(viewLifecycleOwner, {
                            val infoList = mutableListOf<Notification>()
                            for (item in it) {
                                if(item.type == "info") {
                                    infoList.add(item)
                                }
                            }
                            notiAdapter.list = infoList
                        })
                        notiAdapter.notifyDataSetChanged()

                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }


    private fun initAdapter() {
        notiAdapter = NotificationAdapter()


        mainViewModel.notificationList.observe(viewLifecycleOwner, {
            notiAdapter.list = it
        })

        binding.notiFragmentRv.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
            adapter = notiAdapter
            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        val notiRvHelperCallback = NotiRvHelperCallback(notiAdapter).apply {
            setClamp(resources.displayMetrics.widthPixels.toFloat() / 4)
        }

        ItemTouchHelper(notiRvHelperCallback).attachToRecyclerView(binding.notiFragmentRv)

        binding.notiFragmentRv.setOnTouchListener { v, event ->
            notiRvHelperCallback.removePreviousClamp(binding.notiFragmentRv)
            false
        }

        notiAdapter.setOnItemClickListener(object: NotificationAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, id: Int) {
                notiDelete(id, position)
            }
        })

    }

    private fun notiDelete(id: Int, position: Int) {
        var response: Response<HashMap<String, Any>>
        runBlocking {
            response = NotificationService().deleteNotiById(id)
        }
        if(response.code() == 200) {
            val res = response.body()
            if(res != null) {
                if(res["isSuccess"] == true) {
                    notiAdapter.removeData(position)
                    runBlocking {
                        mainViewModel.getNotification(ApplicationClass.sharedPreferencesUtil.getUser().id)
                    }
                    showCustomToast("삭제되었습니다.")
                } else {
                    showCustomToast("삭제 실패")
                }
            }
        } else {
            showCustomToast("서버 통신 오류")
            Log.e(TAG, "notiDelete: ${response.code()} ${response.message()}", )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNav(false)
    }


}