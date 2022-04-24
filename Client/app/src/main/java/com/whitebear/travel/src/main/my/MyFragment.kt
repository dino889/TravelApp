package com.whitebear.travel.src.main.my

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.whitebear.travel.R
import com.whitebear.travel.config.BaseFragment
import com.whitebear.travel.databinding.FragmentMyBinding
import com.whitebear.travel.src.main.MainActivity

/**
 * @author Jiwoo Choi
 * @since 04/02/22
 */
class MyFragment : BaseFragment<FragmentMyBinding>(FragmentMyBinding::bind,R.layout.fragment_my) {
    private lateinit var mainActivity : MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.user = mainViewModel.loginUserInfo.value
        initTabAdapter()
        editProfileBtnClickEvent()
        settingBtnClickEvent()
    }

    private fun initTabAdapter() {
        val viewPagerAdapter = MyTabPageAdapter(this)
        val tabList = listOf("최근 조회", "찜하기")

        viewPagerAdapter.addFragment(RecentlyKeywordFragment())
        viewPagerAdapter.addFragment(LikePlaceFragment())

        binding.myPageFragmentVp.adapter = viewPagerAdapter
        TabLayoutMediator(binding.myPageFragmentTabLayout, binding.myPageFragmentVp) { tab, position ->
            tab.text = tabList[position]
        }.attach()
    }

    /**
     * 프로필 편집 텍스트 클릭 이벤트
     */
    private fun editProfileBtnClickEvent() {
        binding.myPageFragmentTvEditProfile.setOnClickListener {
            this@MyFragment.findNavController().navigate(R.id.editProfileFragment)
        }
    }

    /**
     * 환경설정 버튼 클릭 이벤트
     */
    private fun settingBtnClickEvent() {
        binding.myPageFragmentBtnSetting.setOnClickListener {
            this@MyFragment.findNavController().navigate(R.id.settingFragment)
        }
    }

}